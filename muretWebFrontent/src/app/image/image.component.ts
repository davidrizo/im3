import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Im3wsService} from '../im3ws.service';
import {ActivatedRoute} from '@angular/router';
import {Image} from '../model/image';
import {Page} from '../model/page';
import {BoundingBox} from '../model/bounding-box';
import {NGXLogger} from 'ngx-logger';
import {ResizedEvent} from 'angular-resize-event/resized-event';
import {Region} from '../model/region';
import {Project} from '../model/project';
import {SessionDataService} from '../session-data.service';
import {ComponentCanDeactivate} from '../component-can-deactivate';
import {ImageToolBarService} from '../image-tool-bar/image-tool-bar.service';
import {SVGCanvasComponent, SVGCanvasState, SVGMousePositionEvent} from '../svgcanvas/components/svgcanvas/svgcanvas.component';
import {ShapeComponent} from '../svgcanvas/components/shape/shape.component';
import {LineComponent} from '../svgcanvas/components/line/line.component';

@Component({
  selector: 'app-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.css']
})

export class ImageComponent extends ComponentCanDeactivate implements OnInit, AfterViewInit {
  canvasCursor = 'default';

  image: Image;
  imageURL: string;
  project: Project;

  @ViewChild('domImage')
  private domImage: ElementRef;

  private projectURLs: string;
  private scale: number;
  domImageHeight: number;
  domImageWidth: number;
  domImagePaddingLeft: number;

  @ViewChild('svgCanvas') svgCanvas: SVGCanvasComponent;
  private interactionLine: LineComponent;

  constructor(
    private im3wsService: Im3wsService,
    private sessionDataService: SessionDataService,
    private route: ActivatedRoute,
    private logger: NGXLogger,
    private toolbarService: ImageToolBarService
  ) {
    super();
    this.project = sessionDataService.currentProject;
    this.image = sessionDataService.currentImage;
    this.imageURL = sessionDataService.currentImageMastersURL + '/' + this.image.filename;
    this.logger.debug('Working with image ' + this.imageURL);
  }

  ngOnInit() {
    this.logger.debug('fullImageSVGCanvas= ' + this.svgCanvas);
  }

  private initToolBarInteraction() {
    // this way, when a new image is opened, despite the previous selected mode, the documentAnalysis is selected
    this.toolbarService.currentActivePanel = 'documentAnalysisMode';
    this.activateSelectMode();

    this.toolbarService.selectedTool$.subscribe(next => {
      switch (next) {
        case '101': // select
          this.activateSelectMode();
          break;
        case '102': // split pages
          this.drawInteractiveVerticalLine();
          this.canvasCursor = 'col-resize';
          break;
        case '103':  // split regions
          this.drawInteractiveHorizontalLine();
          this.canvasCursor = 'row-resize';
          break;
      }
    });
  }

  private activateSelectMode() {
    this.canvasCursor = 'default';
    this.toolbarService.selectedTool = '101';
    this.svgCanvas.changeState(SVGCanvasState.eEditing);
  }


  public ngAfterViewInit(): void {
    this.logger.debug('ngAfterViewInit');
    this.initToolBarInteraction();
    this.toolbarService.clearDocumentAnalysisEvent.subscribe(next => {
      this.clearDocumentAnalysis();
    });
  }

  /* It draws the page and region bounding boxes */
  /* private setImage(serviceImage: Image) {
    this.image = serviceImage;
    this.logger.debug('Setting image ' + serviceImage + ' ' + this.image.filename);
    this.imageURL = this.projectURLs + '/' + this.image.filename;
  } */

  onImageLoad() {
    this.logger.debug('OnImageLoad');
    this.scale = this.domImage.nativeElement.width / this.domImage.nativeElement.naturalWidth;
    this.logger.debug('Using scale ' + this.scale);
    this.domImageHeight = this.domImage.nativeElement.height;
    this.domImageWidth = this.domImage.nativeElement.width;
    this.domImagePaddingLeft = this.domImage.nativeElement.paddingLeft;
    this.drawBoundingBoxes();

    /// TO-DO Al cambiar de modo this.drawStaff();
  }

  onResized(event: ResizedEvent): void {
    /*this.logger.debug('Resized');
    if (this.imageSurface) {
      this.scale = this.domImage.nativeElement.width / this.domImage.nativeElement.naturalWidth;
      this.drawBoundingBoxes();
    } // else it is invoked before ngAfterViewInit*/
  }

  private drawBoundingBox(object: any, boundingBox: BoundingBox, targetScale: number,
                          strokeColor: string, strokeWidth: number) {

    const rx = boundingBox.fromX * targetScale;
    const ry = boundingBox.fromY * targetScale;
    const rwidth = targetScale * (boundingBox.toX - boundingBox.fromX);
    const rheight = targetScale * (boundingBox.toY - boundingBox.fromY);

    const rectangle = this.svgCanvas.drawRectangle(rx, ry, rwidth, rheight);
    rectangle.shape.shapeProperties.fillColor = 'transparent';
    rectangle.shape.shapeProperties.stroke = true;
    rectangle.shape.shapeProperties.strokeColor = strokeColor;
    rectangle.shape.shapeProperties.strokeWidth = strokeWidth;
  }


  private drawBoundingBoxes() {
    this.drawImagePages();
  }


  canDeactivate(): boolean {
    return false; // TODO
  }

  private splitPage(imageX: number) {
    this.logger.debug('Splitting page at X: ' + imageX);
    const prevCursor = this.canvasCursor;
    this.canvasCursor = 'wait';
    try {
      this.im3wsService.splitPage(this.image.id, imageX).subscribe(next => {
        this.image.pages = next;
        this.drawImagePages();
        this.canvasCursor = prevCursor;
        this.clearInteractiveLines();
      });
    } catch (e) {
      this.canvasCursor = prevCursor;
      this.clearInteractiveLines();
    }
  }

  private splitRegion(imageX: number, imageY: number) {
    this.logger.debug('Splitting region at X: ' + imageX + ', Y: ' + imageY);
    const prevCursor = this.canvasCursor;
    this.canvasCursor = 'wait';
    try {
      this.im3wsService.splitRegion(this.image.id, imageX, imageY).subscribe(next => {
        this.image.pages = next;
        this.drawImagePages();
        this.canvasCursor = prevCursor;
        this.clearInteractiveLines();
      });
    } catch (e) {
      this.canvasCursor = prevCursor;
      this.clearInteractiveLines();
    }
  }

  clearDocumentAnalysis() {
    if (confirm('Do you really want to clear the document analysis?')) {
      this.im3wsService.clearDocumentAnalysis(this.image.id).subscribe(next => {
        this.image.pages = next;
        this.drawImagePages();
        this.clearInteractiveLines();
      });
    }
  }

  private drawInteractiveVerticalLine() {
    this.interactionLine = <LineComponent>this.svgCanvas.drawLine(0, 0, 0, this.domImageWidth);
    this.interactionLine.shape.shapeProperties.strokeColor = 'red';
    this.interactionLine.shape.shapeProperties.strokeWidth = 2;
    this.svgCanvas.changeState(SVGCanvasState.eIdle);
  }

  private drawInteractiveHorizontalLine() {
    this.interactionLine = <LineComponent>this.svgCanvas.drawLine(0, 0, this.domImageWidth, 0);
    this.interactionLine.shape.shapeProperties.strokeColor = 'red';
    this.interactionLine.shape.shapeProperties.strokeWidth = 2;
    this.svgCanvas.changeState(SVGCanvasState.eIdle);
  }

  private drawImagePages() {
    this.svgCanvas.clear();
    this.logger.debug('Drawing bounding boxes for image ' + this.image);
    this.image.pages.forEach(page => {
      this.logger.debug('Page ' + page);
      this.drawBoundingBox(page, page.boundingBox, this.scale, 'red', 12);
      page.regions.forEach(region => {
        this.logger.debug('Region ' + region);
        this.drawBoundingBox(region, region.boundingBox, this.scale, 'green', 3);
      });
    });
  }

  private clearInteractiveLines() {
    this.svgCanvas.remove(this.interactionLine);
  }

  onImageMouseDown(event) {
    this.logger.debug('Image clicked: ' + event);
    switch (this.toolbarService.selectedTool) {
      case '102':
        this.splitPage(event.layerX / this.scale);
        break;
      case '103':
        this.splitRegion(event.layerX / this.scale, event.layerY / this.scale);
        break;
    }
  }


  onMouseEvent($event: SVGMousePositionEvent) {
    if ($event.mouseEvent.type === 'mousemove') {
      switch (this.toolbarService.selectedTool) {
        case '102':
          // this.splitLine.transform(transform().translate(event.layerX, 0));
          this.interactionLine.moveHorizontallyTo($event.svgPosition.x);
          break;
        case '103':
          this.interactionLine.moveVerticallyTo($event.svgPosition.y);
          // this.splitLine.transform(transform().translate(0, event.layerY));
          break;
      }
    } else if ($event.mouseEvent.type === 'mousedown') {
      switch (this.toolbarService.selectedTool) {
        case '102':
          this.splitPage($event.svgPosition.x / this.scale);
          break;
        case '103':
          this.splitRegion($event.svgPosition.x / this.scale, $event.svgPosition.y / this.scale);
          break;
      }
    }
  }

  onShapeCreated($event: ShapeComponent) {
    // TODO para crear objetos dibujando
  }

  onShapeChanged($event: ShapeComponent) {
    // TODO localizar el ID del objeto para redimensionarlo
  }
}
