import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ProjectService} from '../projects/project.service';
import {ActivatedRoute} from '@angular/router';
import {Image} from '../model/image';

import {geometry, Surface, Path, Text, Group, Rect, ShapeOptions} from '@progress/kendo-drawing';
import {forEach} from '@angular/router/src/utils/collection';
import {Page} from '../model/page';
import {Symbol} from '../model/symbol';
import {BoundingBox} from '../model/bounding-box';
import {Point, Size} from '@progress/kendo-drawing/geometry';
import {MessageService} from '../messages/message.service';
import {isNullOrUndefined} from 'util';
import {Stroke} from '../model/stroke';

@Component({
  selector: 'app-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.css']
})
export class ImageComponent implements OnInit, AfterViewInit, OnDestroy {
  image: Image;
  @ViewChild('imageSurface')
  private imageSurfaceElement: ElementRef;
  private imageSurface: Surface;

  @ViewChild('agnosticSurface')
  private agnosticSurfaceElement: ElementRef;
  private agnosticSurface: Surface;

  svgOfSymbols: Array<String> = [];

  constructor(
    private projectService: ProjectService,
    private route: ActivatedRoute,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    const routeParams = this.route.snapshot.params;

    this.projectService.getImage$(routeParams.id).
    subscribe(serviceImage => this.setImage(serviceImage));
  }

  /*private drawScene(surface: Surface) {
    const path = new Path({
      stroke: {
        color: '#9999b6',
        width: 2
      }
    });
    path.moveTo(0, 0)
      .lineTo(150, 0).lineTo(150, 65).lineTo(0, 65)
      .close();
    const text = new Text(
      'Prueba',
      new Point(60, 25),
      {font: 'bold 15px Arial'}
    );
    const group = new Group();
    group.append(path);
    group.append(text);

    // Translate the group.
    group.transform(
      transform().translate(50, 50)
    );

    // Render the group on the surface.
    surface.draw(group);
  }*/

  public ngOnDestroy() {
    this.imageSurface.destroy();
  }

  private createSurface() {
    this.log('Creating surfaces');

    // Obtain a reference to the native DOM element of the wrapper
    const element = this.imageSurfaceElement.nativeElement;

    // Create a drawing surface
    this.imageSurface = Surface.create(element);

    // Obtain a reference to the native DOM element of the wrapper
    const elementAgnostic = this.agnosticSurfaceElement.nativeElement;

    // Create a drawing surface
    this.agnosticSurface = Surface.create(elementAgnostic);
  }

  public ngAfterViewInit(): void {
    this.createSurface();
    this.drawBoundingBoxes();
    this.drawAgnosticTranscription();
  }

  /* It draws the page and region bounding boxes */
  private setImage(serviceImage: Image) {
    this.log('Setting image ' + serviceImage);
    this.image = serviceImage;
  }

  private drawBoundingBox(boundingBox: BoundingBox, color: string) {
    this.log('Drawing ' + boundingBox.toString() + ' in color ' + color);
    const geometryRect = new geometry.Rect(new Point(boundingBox.fromX, boundingBox.fromY),
new Size(boundingBox.toX - boundingBox.fromX, boundingBox.toY - boundingBox.fromY));
    const rect = new Rect(geometryRect, {
      stroke: { color: color, width: 2 }
    });

    this.imageSurface.draw(rect);
  }

  /** Log a message with the MessageService */
  private log(message: string) {
    this.messageService.add(`ImageComponent: ${message}`);
  }

  private drawBoundingBoxes() {
    this.log('Drawing bounding boxes for image ' + this.image);
    this.image.pages.forEach(page => {
      this.log('Page ' + page);
      this.drawBoundingBox(page.boundingBox, 'red');
      page.regions.forEach(region => {
        this.log('Region ' + region);
        this.drawBoundingBox(region.boundingBox, 'green');
      });
    });
  }

  private drawAgnosticTranscription() {
    this.drawStaff();
    this.drawAgnosticSymbols();
  }

  private drawSymbol(symbol: Symbol) {
    this.drawSymbolStrokes(symbol);
    this.drawSymbolAgnostic(symbol);
  }

  /* TODO Posición */
  private drawSymbolAgnostic(symbol: Symbol) {
    this.log('Drawing SVG ' + symbol);
    this.svgOfSymbols.push('https://upload.wikimedia.org/wikipedia/commons/f/fb/C_%28indication_de_mesure%29.svg');
    /*TODO mapa desde symbol.getAgnosticSymbolType - vaciar primero?*/
  }

  private drawSymbolStrokes(symbol: Symbol) {
    this.log('Drawing symbol ' + symbol.id);
    if (!isNullOrUndefined(symbol.strokes)) {
      this.log('Drawing ' + symbol.strokes.strokeList.length + ' strokes');
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
    const pathOptions: ShapeOptions = {
      stroke: {
        color: '#000000',
        width: 2
      }
    };

    let i = 0;
    for (i = 0 ; i < 5; i++) {
      const line = new Path(pathOptions);
      line.moveTo(0, i * 10)
        .lineTo(400, i * 10)
        .close();
      this.agnosticSurface.draw(line);
    }
  }

  private drawAgnosticSymbols() {
    /*TODO Seleccionar un pentagrama!!! */
    // now draw symbols
    this.image.pages[0].regions[0].symbols.forEach(symbol => {
      this.drawSymbol(symbol);
    });
  }
}
