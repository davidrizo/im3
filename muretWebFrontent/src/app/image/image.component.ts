import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Im3wsService} from '../im3ws.service';
import {ActivatedRoute} from '@angular/router';
import {geometry, Surface, Path, Text, Group, Rect, ShapeOptions, Image as KendoImage} from '@progress/kendo-drawing';
import {Image} from '../model/image';
import {Page} from '../model/page';
import {Symbol} from '../model/symbol';
import {BoundingBox} from '../model/bounding-box';
import {Point, Size} from '@progress/kendo-drawing/geometry';
import {isNullOrUndefined} from 'util';
import {Stroke} from '../model/stroke';
import {NGXLogger} from 'ngx-logger';
import { ResizedEvent } from 'angular-resize-event/resized-event';
import {Region} from '../model/region';
import {AgnosticSymbolSVGPath} from './agnostic-symbol-svgpath';
import {Scales} from '../model/scales';
import {max} from 'rxjs/operators';
import {Project} from '../model/project';
import {SessionDataService} from '../session-data.service';
import {ComponentCanDeactivate} from '../component-can-deactivate';

@Component({
  selector: 'app-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.css']
})

export class ImageComponent extends ComponentCanDeactivate implements OnInit, AfterViewInit, OnDestroy {
  image: Image;
  imageURL: string;
  selectedStaffImageURL: string;
  project: Project;
  
  private agnosticStaffHeight: number;
  private agnosticStaffWidth: number;

  selectedStaffWidth: number;
  selectedStaffHeight: number;
  selectedStaffImageBackgroundPositionX: number;
  selectedStaffImageBackgroundPositionY: number;
  selectedStaffImageBackgroundPertentage: number;
  staffSelected: boolean;

  @ViewChild('selectedStaffSurface')
  private selectedStaffSurfaceElement: ElementRef;
  private selectedStaffSurface: Surface;

  @ViewChild('imageSurface')
  private imageSurfaceElement: ElementRef;
  private imageSurface: Surface;

  /* @ViewChild('agnosticSurface')
  private agnosticSurfaceElement: ElementRef;
  private agnosticSurface: Surface; */

  @ViewChild('domImage')
  private domImage: ElementRef;

  // svgOfSymbols: Array<string> = [];
  private projectURLs: string;
  private scale: number;
  domImageHeight: number;
  domImageWidth: number;
  domImagePaddingLeft: number;
  private boundingBoxesGroup: Group;
  private selectedElementGroup: Group;
  private selectedStaffBoundingBoxesGroup: Group;
  private selectedStaffFinalScale: number;
  private expectedStaffWidthPercentage: number;
  private selectedStaffScale: number;

  staffLineYCoordinates: number[];
  agnosticSymbolSVGMap: Map<string, string>;
  agnosticSymbolSVGs: Array<AgnosticSymbolSVGPath>;
  agnosticSVGScaleX: number;
  agnosticSVGScaleY: number;
  agnosticSVGem: number;
  staffSpaceHeight: number;
  staffMargin: number;
  private staffBottomLineY: number;

  constructor(
    private im3wsService: Im3wsService,
    private sessionDataService: SessionDataService,
    private route: ActivatedRoute,
    private logger: NGXLogger
  ) {
    super();
    this.agnosticSymbolSVGMap = new Map();
    this.project = sessionDataService.currentProject;
  }

  ngOnInit() {
    const routeParams = this.route.snapshot.params;

    this.projectURLs = routeParams.projectURLs;
    this.logger.debug('Image id=' + routeParams.id);
    this.logger.debug('Project URLs=' + this.projectURLs);
    this.im3wsService.getImage$(routeParams.id).
      subscribe(serviceImage => this.setImage(serviceImage)
    );

    // TODO notationType y manuscriptType
    this.im3wsService.getSVGScales$(this.project.notationType, this.project.manuscriptType).
    subscribe(next => {
      this.agnosticSVGScaleX = next.x;
      this.agnosticSVGScaleY = next.y;
      this.agnosticSVGem = next.em;
      this.staffSpaceHeight = this.agnosticSVGem / 4.0;
      this.logger.debug('Using SVG scales: (' + this.agnosticSVGScaleX + ', ' + this.agnosticSVGScaleY
        + '), with default em=' + this.agnosticSVGem);
      }
    );

  }

  public ngAfterViewInit(): void {
    this.logger.debug('ngAfterViewInit');
    this.createSurfaces();
  }


  public ngOnDestroy() {
    this.imageSurface.destroy();
  }

  /* It draws the page and region bounding boxes */
  private setImage(serviceImage: Image) {
    this.image = serviceImage;
    this.logger.debug('Setting image ' + serviceImage + ' ' + this.image.filename);
    this.imageURL = this.projectURLs + '/' + this.image.filename;
  }

  onImageLoad() {
    this.logger.debug('OnImageLoad');
    this.scale = this.domImage.nativeElement.width / this.domImage.nativeElement.naturalWidth;
    this.logger.debug('Using scale ' + this.scale);
    this.domImageHeight = this.domImage.nativeElement.height;
    this.domImageWidth = this.domImage.nativeElement.width;
    this.domImagePaddingLeft = this.domImage.nativeElement.paddingLeft;
    this.drawBoundingBoxes();
    this.drawStaff();
  }

  onResized(event: ResizedEvent): void {
    this.logger.debug('Resized');
    if (this.imageSurface) {
      this.scale = this.domImage.nativeElement.width / this.domImage.nativeElement.naturalWidth;
      this.drawBoundingBoxes();
    } // else it is invoked before ngAfterViewInit
  }

  private createSurfaces() {
    this.logger.debug('Creating surfaces');

    // Obtain a reference to the native DOM element of the wrapper
    const element = this.imageSurfaceElement.nativeElement;

    // Create a drawing surface
    this.imageSurface = Surface.create(element,  {

    });

    // Obtain a reference to the native DOM element of the wrapper
    // const elementAgnostic = this.agnosticSurfaceElement.nativeElement;

    // Create a drawing surface
    // this.agnosticSurface = Surface.create(elementAgnostic, {
    // });

    // Obtain a reference to the native DOM element of the wrapper
    const elementSelectedStaff = this.selectedStaffSurfaceElement.nativeElement;

    // Create a drawing surface
    this.selectedStaffSurface = Surface.create(elementSelectedStaff, {
    });

    this.selectedStaffBoundingBoxesGroup = new Group();
    this.selectedStaffSurface.draw(this.selectedStaffBoundingBoxesGroup);
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
      this.imageSurface.clear();
    }
    this.selectedElementGroup = new Group();
    this.imageSurface.draw(this.selectedElementGroup);
    this.boundingBoxesGroup = new Group();
    this.logger.debug('Drawing bounding boxes for image ' + this.image);
    this.image.pages.forEach(page => {
      this.logger.debug('Page ' + page);
      this.drawBoundingBox(this.boundingBoxesGroup, page, page.boundingBox, this.scale, 'red', 12);
      page.regions.forEach(region => {
        this.logger.debug('Region ' + region);
        this.drawBoundingBox(this.boundingBoxesGroup, region, region.boundingBox, this.scale, 'green', 3);
      });
    });

    this.imageSurface.draw(this.boundingBoxesGroup);

    // Bind to the mouseenter event
    this.imageSurface.bind('click', (args: any) => {
      const element = args.element;
      if (element) {
        this.logger.debug('Clicked on ' + element.object.constructor.name + ' ' + element.object.id);
        const bbox = element.bbox();

        const rect = new Rect(bbox, {
          fill: {color: 'red', opacity: 0.3}
        });

        this.selectedElementGroup.clear();
        this.selectedElementGroup.append(rect);

        if (element.object.symbols) { // if it is a region
          this.doSelect(element.object);
        }

        /*
        // Obtain the element offset in order to calculat the absolute position on the page
        const offset = this.surfaceElement.nativeElement.getBoundingClientRect();

        // Update the content and show the popup
        this.content = element.options.tooltipContent;
        this.show = true;

        // Set the Popup offset based on the position of the shape and the element offset
        this.offset = {
          left: bbox.center().x + offset.left,
          top: bbox.origin.y + offset.top
        };*/
      }
    });
  }

  private doSelect(region: Region) {
    this.staffSelected = true;
    this.drawSelectedRegion(region);
    this.drawSelectedRegionSymbolBoxes(region);
    this.drawSelectedRegionAgnosticSymbols(region);

    /* this.im3wsService.getSymbolsOfRegion$(region.id).subscribe(next => {
    }); */
  }


  private drawSymbol(symbol: Symbol) {
    this.drawSymbolStrokes(symbol);
  }

  private drawSymbolStrokes(symbol: Symbol) {
    this.logger.debug('Drawing symbol ' + symbol.id);
    if (!isNullOrUndefined(symbol.strokes)) {
      this.logger.debug('Drawing ' + symbol.strokes.strokeList.length + ' strokes');
      symbol.strokes.strokeList.forEach(stroke => {
        this.drawStroke(stroke);
      });
    }
  }

  private drawStroke(stroke: Stroke) {
    const pathOptions: ShapeOptions = {
      stroke: {
        color: '#00FF00',
        width: 1
      }
    };

    const path = new Path(pathOptions);
    let first = true;
    stroke.points.forEach(point => {
      if (first) {
        first = false;
        path.moveTo(point.x, point.y);
      } else {
        path.lineTo(point.x, point.y);
      }
    });
    path.close();
    this.imageSurface.draw(path);
  }

  private drawStaff() {
    this.staffMargin = this.agnosticSVGem;
    this.staffLineYCoordinates = new Array(5);
    let i = 0;
    for (i = 0 ; i < 5; i++) {
      const y = i * this.staffSpaceHeight + this.staffMargin;
      this.staffLineYCoordinates[i] = y;
      if (i === 4) {
        this.staffBottomLineY = y;
      }
    }
    this.agnosticStaffHeight = this.staffMargin * 2 + this.staffSpaceHeight * 5;
    this.agnosticSymbolSVGs = new Array();
  }

  private drawSelectedRegion(region: Region) {
    this.selectedStaffImageURL = this.imageURL;
    const regionWidth = region.boundingBox.toX - region.boundingBox.fromX;
    const regionHeight = region.boundingBox.toY - region.boundingBox.fromY;
    this.selectedStaffWidth = this.selectedStaffSurfaceElement.nativeElement.offsetWidth;
    this.selectedStaffScale = this.domImage.nativeElement.naturalWidth / regionWidth;
    const expectedStaffWidth = regionWidth / this.selectedStaffScale;
    this.expectedStaffWidthPercentage = this.selectedStaffWidth / expectedStaffWidth;
    this.selectedStaffImageBackgroundPertentage = this.selectedStaffScale * 100.0;
    this.selectedStaffImageBackgroundPositionX = this.expectedStaffWidthPercentage * (-region.boundingBox.fromX / this.selectedStaffScale);
    this.selectedStaffImageBackgroundPositionY = this.expectedStaffWidthPercentage * (-region.boundingBox.fromY / this.selectedStaffScale);
    this.selectedStaffHeight = (regionHeight / this.selectedStaffScale) * this.expectedStaffWidthPercentage;
    this.selectedStaffFinalScale = this.selectedStaffScale;
    this.logger.debug('Selected staff scale: ' + this.selectedStaffScale);
  }

  private drawSelectedRegionSymbolBoxes(region: Region) {
    this.logger.debug('Drawing region symbol boxes');
    this.selectedStaffBoundingBoxesGroup.clear();
    if (region.symbols) {
      region.symbols.forEach(symbol => {
        this.logger.debug('Drawing symbol ' + symbol);

        const fromX = ((symbol.boundingBox.fromX - region.boundingBox.fromX) / this.selectedStaffScale) * this.expectedStaffWidthPercentage;
        const fromY = ((symbol.boundingBox.fromY - region.boundingBox.fromY) / this.selectedStaffScale) * this.expectedStaffWidthPercentage;
        const toX = ((symbol.boundingBox.toX - region.boundingBox.fromX) / this.selectedStaffScale) * this.expectedStaffWidthPercentage;
        const toY = ((symbol.boundingBox.toY - region.boundingBox.fromY) / this.selectedStaffScale) * this.expectedStaffWidthPercentage;
        /*const fromX = 0;
        const fromY = 0;
        const toX = this.selectedStaffWidth;
        const toY = this.selectedStaffHeight;*/

        const translatedAndScaledBoundingBox = new BoundingBox(
          fromX, fromY, toX, toY
        );

        this.drawBoundingBox(this.selectedStaffBoundingBoxesGroup, symbol, translatedAndScaledBoundingBox,
          1,
          'blue', 1);
      });
    }
  }

  /* TODO Posición */
  private drawSymbolAgnostic(region: Region, symbol: Symbol) {
    this.logger.debug('Drawing SVG ' + symbol);
    if (symbol.agnosticSymbolType) {
      this.agnosticStaffWidth = this.selectedStaffWidth;
      const x = ((symbol.boundingBox.fromX - region.boundingBox.fromX) / this.selectedStaffScale) * this.expectedStaffWidthPercentage;
      const lineSpace = this.positionInStaffToLineSpace(symbol.positionInStaff);
      const heightDifference = -(this.staffSpaceHeight * (lineSpace / 2.0));
      const y = this.staffBottomLineY  + heightDifference;
      const svg = this.agnosticSymbolSVGMap.get(symbol.agnosticSymbolType);
      if (!svg) {
        // TODO Tipo de notación y manuscrito
        this.im3wsService.getSVGFromAgnosticSymbolType$(
          this.project.notationType,
          this.project.manuscriptType,
          symbol.agnosticSymbolType).subscribe(next => {
            const svgD = next.response;
          this.agnosticSymbolSVGMap.set(symbol.agnosticSymbolType, svgD);
          const asvg = new AgnosticSymbolSVGPath(svgD, x, y);
          this.agnosticSymbolSVGs.push(asvg);
        });
      } else {
        const asvg = new AgnosticSymbolSVGPath(svg, x, y);
        this.agnosticSymbolSVGs.push(asvg);
      }
    }
  }

  private drawSelectedRegionAgnosticSymbols(region: Region) {
    this.agnosticSymbolSVGs = new Array(); // empty it
    region.symbols.forEach(symbol => {
      this.drawSymbolAgnostic(region, symbol);
    });
  }

  // TODO Sacar todo esto a componentes
  private positionInStaffToLineSpace(positionInStaff: string): number {
    const value = Number(positionInStaff.substr(1));
    if (positionInStaff.charAt(0) === 'L') {
      return (value - 1) * 2;
    } else if (positionInStaff.charAt(0) === 'S') {
      return (value) * 2 - 1;
    } else {
      throw new Error('Invalid positionInStaff, it should start with L or S: ' + positionInStaff);
    }
  }

  canDeactivate(): boolean {
    return false; // TODO
  }
}
