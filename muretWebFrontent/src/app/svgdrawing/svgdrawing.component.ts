import {Component, ComponentFactoryResolver, Injector, OnChanges, OnInit, SimpleChanges, ViewChild, ViewContainerRef} from '@angular/core';
import {ShapeProperties} from '../svgcanvas/model/shape';
import {ShapeType, ToolType} from '../svgcanvas/model/shape-types';
import {LineComponent} from '../svgcanvas/components/line/line.component';
import {CircleComponent} from '../svgcanvas/components/circle/circle.component';
import {RectangleComponent} from '../svgcanvas/components/rectangle/rectangle.component';
import {SquareComponent} from '../svgcanvas/components/square/square.component';
import {EllipseComponent} from '../svgcanvas/components/ellipse/ellipse.component';
import {TextComponent} from '../svgcanvas/components/text/text.component';
import {ImageComponent} from '../svgcanvas/components/image/image.component';
import {PolyLineComponent} from '../svgcanvas/components/polyline/polyline.component';
import {PathComponent} from '../svgcanvas/components/path/path.component';
import {SVGCanvasComponent, SVGCanvasState} from '../svgcanvas/components/svgcanvas/svgcanvas.component';
import {ShapeComponent} from '../svgcanvas/components/shape/shape.component';


@Component({
  selector: 'app-svgdrawing-component',
  templateUrl: './svgdrawing.component.html',
  styleUrls: ['./svgdrawing.component.css']
})
export class SVGDrawingComponent implements OnInit, OnChanges {
  title = 'SVG Drawing Tool';

  @ViewChild('svgCanvas') svgCanvas: SVGCanvasComponent;

  shapeProperties: ShapeProperties = new ShapeProperties();
  shapeValue: string;

  constructor() {
    console.log('SVGDrawing constructor');
  }

  ngOnInit(): void {
    console.log('svgCanvas = ' + this.svgCanvas);
  }

  clearShapes(): void {
    this.svgCanvas.clear();
  }

  selectTool(toolType: string): void {
    if (toolType === 'Pointer') {
      this.svgCanvas.changeState(SVGCanvasState.eSelecting);
    } else if (toolType === 'Move') {
      this.svgCanvas.changeState(SVGCanvasState.eEditing);
    }
    console.log('selected tool:', toolType);
  }

  selectShape(shapeType$: any) {
    this.svgCanvas.selectShape(shapeType$);
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

}
