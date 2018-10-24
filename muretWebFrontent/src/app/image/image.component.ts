import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ProjectService} from '../projects/project.service';
import {ActivatedRoute} from '@angular/router';
import {Image} from '../model/image';

import {geometry, Surface, Path, Text, Group, Rect} from '@progress/kendo-drawing';
import {forEach} from '@angular/router/src/utils/collection';
import {Page} from '../model/page';
import {BoundingBox} from '../model/bounding-box';
import {Point, Size} from '@progress/kendo-drawing/geometry';
import {MessageService} from '../messages/message.service';

@Component({
  selector: 'app-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.css']
})
export class ImageComponent implements OnInit, AfterViewInit, OnDestroy {
  image: Image;
  @ViewChild('surface')
  private surfaceElement: ElementRef;
  private surface: Surface;

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
    this.surface.destroy();
  }

  private createSurface() {
    this.log('Creating surface');

    // Obtain a reference to the native DOM element of the wrapper
    const element = this.surfaceElement.nativeElement;

    // Create a drawing surface
    this.surface = Surface.create(element);

  }

  public ngAfterViewInit(): void {
    this.createSurface();
    this.drawBoundingBoxes();
  }

  /* It draws the page and region bounding boxes */
  private setImage(serviceImage: Image) {
    this.log('Setting image ' + serviceImage);
    this.image = serviceImage;
  }

  private drawBoundingBox(boundingBox: BoundingBox) {
    const geometryRect = new geometry.Rect(new Point(boundingBox.fromX, boundingBox.fromY),
new Size(boundingBox.toX - boundingBox.fromX, boundingBox.toY - boundingBox.fromY));
    const rect = new Rect(geometryRect, {
      stroke: { color: 'red', width: 1 }
    });

    this.surface.draw(rect);
  }

  /** Log a message with the MessageService */
  private log(message: string) {
    this.messageService.add(`ImageComponent: ${message}`);
  }

  private drawBoundingBoxes() {
    this.log('Drawing bounding boxes for image ' + this.image);
    this.image.pages.forEach(page => {
      this.log('Page ' + page);
      this.drawBoundingBox(page.boundingBox);
      page.regions.forEach(region => {
        this.log('Region ' + region);
        this.drawBoundingBox(region.boundingBox);
      });
    });
  }
}
