import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild, ViewChildren} from '@angular/core';
import {Im3wsService} from '../im3ws.service';
import {ActivatedRoute} from '@angular/router';
import {geometry, Surface, Path, Text, Group, Rect, ShapeOptions, Image as KendoImage} from '@progress/kendo-drawing';
import {Image} from '../model/image';
import {Page} from '../model/page';
import {BoundingBox} from '../model/bounding-box';
import {Point, Size, transform} from '@progress/kendo-drawing/geometry';
import {NGXLogger} from 'ngx-logger';
import { ResizedEvent } from 'angular-resize-event/resized-event';
import {Region} from '../model/region';
import {Project} from '../model/project';
import {SessionDataService} from '../session-data.service';
import {ComponentCanDeactivate} from '../component-can-deactivate';
import {ImageToolBarService} from '../image-tool-bar/image-tool-bar.service';
import {ImageToolBarComponent} from '../image-tool-bar/image-tool-bar.component';

@Component({
  selector: 'app-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.css']
})

export class ImageComponent extends ComponentCanDeactivate implements OnInit, AfterViewInit, OnDestroy {
  canvasCursor = 'default';

  image: Image;
  imageURL: string;
  project: Project;

  @ViewChild('imageSurface')
  private imageSurfaceElement: ElementRef;
  private imageSurface: Surface;
  private imageSurfaceInteractive: Group;

  @ViewChild('domImage')
  private domImage: ElementRef;

  private projectURLs: string;
  private scale: number;
  domImageHeight: number;
  domImageWidth: number;
  domImagePaddingLeft: number;
  private boundingBoxesGroup: Group;

  private splitLine: Path; // used in split page or split region mode

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
    /* const routeParams = this.route.snapshot.params;

    this.projectURLs = routeParams.projectURLs;
    this.logger.debug('Image id=' + routeParams.id);
    this.logger.debug('Project URLs=' + this.projectURLs);
    this.im3wsService.getImage$(routeParams.id).
      subscribe(serviceImage => this.setImage(serviceImage)
    ); */
  }

  private initToolBarInteraction() {
    this.toolbarService.selectedTool$.subscribe(next => {
      this.imageSurfaceInteractive.clear();
      switch (next) {
        case '101': // select
          this.canvasCursor = 'default';
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


  public ngAfterViewInit(): void {
    this.logger.debug('ngAfterViewInit');
    this.createSurfaces();
    this.initToolBarInteraction();
    this.toolbarService.clearDocumentAnalysisEvent.subscribe(next => {
      this.clearDocumentAnalysis();
    });
  }


  public ngOnDestroy() {
    this.imageSurface.destroy();
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

  private createSurfaces() {
    this.logger.debug('Creating surfaces');

    // Obtain a reference to the native DOM element of the wrapper
    const element = this.imageSurfaceElement.nativeElement;

    // Create a drawing surface
    this.imageSurface = Surface.create(element,  {

    });
    this.imageSurfaceInteractive = new Group();
    this.imageSurface.draw(this.imageSurfaceInteractive);
  }

  private boundingBoxToGeometryRect(boundingBox: BoundingBox, targetScale: number) {
    return new geometry.Rect(new Point(boundingBox.fromX * targetScale, boundingBox.fromY * targetScale),
      new Size(targetScale * (boundingBox.toX - boundingBox.fromX),
        targetScale * (boundingBox.toY - boundingBox.fromY)));
  }
  private drawBoundingBox(targetBoundingBoxesGroup: Group, object: any, boundingBox: BoundingBox, targetScale: number,
                          color: string, width: number) {
    /* this.logger.debug('Drawing bounding box ' + boundingBox + ' in color ' + color);
    this.logger.debug('from x=' + boundingBox.fromX + ' - fromY = ' + boundingBox.fromY
      + 'to x=' + boundingBox.toX + ' - toY = ' + boundingBox.toY); */
    const geometryRect = this.boundingBoxToGeometryRect(boundingBox, targetScale);
    const rect: any = new Rect(geometryRect, { // use any in order to be able to add object property dynamically
      stroke: { color: color, width: width},
      fill: {color: color, opacity: 0.0}
    });
    rect.object = object;

    targetBoundingBoxesGroup.append(rect);
  }


  private drawBoundingBoxes() {
    if (this.imageSurface) {
      //// this.imageSurface.clear();
    }
    this.boundingBoxesGroup = new Group();
    this.imageSurface.draw(this.boundingBoxesGroup);
    this.drawImagePages();
  }


  canDeactivate(): boolean {
    return false; // TODO
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

  onImageMouseMove(event) {
    switch (this.toolbarService.selectedTool) {
      case '102':
        this.splitLine.transform(transform().translate(event.layerX, 0));
        break;
      case '103':
        this.splitLine.transform(transform().translate(0, event.layerY));
        break;
    }
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
        this.imageSurfaceInteractive.clear();
      });
    } catch (e) {
      this.canvasCursor = prevCursor;
      this.imageSurfaceInteractive.clear();
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
        this.imageSurfaceInteractive.clear();
      });
    } catch (e) {
      this.canvasCursor = prevCursor;
      this.imageSurfaceInteractive.clear();
    }
  }

  clearDocumentAnalysis() {
    if (confirm('Do you really want to clear the document analysis?')) {
      this.im3wsService.clearDocumentAnalysis(this.image.id).subscribe(next => {
        this.image.pages = next;
        this.drawImagePages();
        this.imageSurfaceInteractive.clear();
      });
    }
  }

  private drawInteractiveVerticalLine() {
    this.splitLine = new Path({
      stroke: {
        color: 'red',
        width: 2
      }
    });
    this.splitLine.moveTo(0, 0)
      .lineTo(0, this.domImageHeight)
      .close();

    this.imageSurfaceInteractive.append(this.splitLine);
  }

  private drawInteractiveHorizontalLine() {
    this.splitLine = new Path({
      stroke: {
        color: 'red',
        width: 2
      }
    });
    this.splitLine.moveTo(0, 0)
      .lineTo(this.domImageWidth, 0)
      .close();

    this.imageSurfaceInteractive.append(this.splitLine);
  }

  private drawImagePages() {
    this.boundingBoxesGroup.clear();
    this.logger.debug('Drawing bounding boxes for image ' + this.image);
    this.image.pages.forEach(page => {
      this.logger.debug('Page ' + page);
      this.drawBoundingBox(this.boundingBoxesGroup, page, page.boundingBox, this.scale, 'red', 12);
      page.regions.forEach(region => {
        this.logger.debug('Region ' + region);
        this.drawBoundingBox(this.boundingBoxesGroup, region, region.boundingBox, this.scale, 'green', 3);
      });
    });
  }

}
