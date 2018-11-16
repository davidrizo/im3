import {Directive, Input, ViewContainerRef, OnInit, OnDestroy} from '@angular/core';
import { ShapeComponent } from '../components/shape/shape.component';
import { ShapeService } from '../service/shape.service';

@Directive({
    selector: '[dynamic-svg]'
})

export class DynamicSvgDirective implements OnInit, OnDestroy {

    @Input() component: ShapeComponent;

    constructor(private viewContainerRef: ViewContainerRef, private shapeService: ShapeService) {
    }

    ngOnInit() {
        console.log('DynamicSvgDirective ngOnInit() - component : ',
          this.component + ' and viewContainerRef=' + this.viewContainerRef);

        const shapeComponent: ShapeComponent = this.shapeService.getShapeComponent();
        this.viewContainerRef.createEmbeddedView(shapeComponent.shapeTemplate);
    }

    ngOnDestroy() {
        console.log('DynamicSvgDirective ngOnDestroy()');
        this.viewContainerRef.clear();
    }
}

