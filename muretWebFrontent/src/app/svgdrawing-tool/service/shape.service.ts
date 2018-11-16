import { Injectable } from '@angular/core';
import { ShapeComponent } from '../components/shape/shape.component';
import {RectangleComponent} from '../components/rectangle/rectangle.component';
import {MousePosition} from '../model/shape';

@Injectable({
    providedIn: 'root'
})
export class ShapeService {

    private shapesComponents: ShapeComponent[] = [];

    private selectedComponent: ShapeComponent;

    constructor() {
        console.log('ShapeService constructor() :', this.selectedComponent);
      const r = new RectangleComponent();
      r.startDrawing(new MousePosition());
      const mp = new MousePosition();
      mp.x = 400;
      mp.y = 400;
      r.draw(mp);
      this.shapesComponents.push(r);
      this.selectedComponent = r;
    }

    getShapeComponents(): ShapeComponent[] {
        return this.shapesComponents;
    }

    removeAllShapeComponents(): void {
        this.shapesComponents = [];
    }

    setShapeComponent(component: ShapeComponent): void {
        this.selectedComponent = component;
        this.shapesComponents.push(component);
        console.log('ShapeService component : ', component);
        console.log('ShapeService shapes : ', this.shapesComponents);
    }

    getShapeComponent(): ShapeComponent {
        return this.selectedComponent;
    }

    findShapeComponent(name: string): ShapeComponent {
        console.log('find name : ', name);
        for (let i = 0; i < this.shapesComponents.length; i++) {
            console.log('FIND JSON : ', JSON.stringify(this.shapesComponents[i].shape));
        }

        return this.shapesComponents.find(x => x.shape.shapeProperties.name === name);
    }
}
