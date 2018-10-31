import {Component, ElementRef, OnInit, QueryList, Renderer2, ViewChild, ViewChildren} from '@angular/core';
import {Im3wsService} from '../im3ws.service';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {Project} from '../model/project';
import {MessageService} from '../messages/message.service';
import {ConfigurationService} from '../configuration.service';
import {ProjectURLS} from '../model/project-urls';
import {DragulaService} from 'ng2-dragula';
import {ImageThumbnailComponent} from '../image-thumbnail/image-thumbnail.component';
// import { Lightbox } from 'ngx-lightbox';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {
  project: Project;
  projectURLs: ProjectURLS;
  BAG = 'DRAGULA_FACTS';
  @ViewChildren(ImageThumbnailComponent) imageThumbnailComponents: QueryList<ImageThumbnailComponent>;
  @ViewChild('domImages') domImages: ElementRef;

  constructor(
    private projectService: Im3wsService,
    private route: ActivatedRoute,
    private messageService: MessageService,
    private configurationService: ConfigurationService,
    private dragulaService: DragulaService,
  ) {
    dragulaService.drop(this.BAG)
      .subscribe(({ el }) => {
        console.log('Images order for drop ' + el );
        console.log('DOM:' + this.domImages.nativeElement);

        let imagesOrder = '';
        let firstImage = true;
        const sortedImages = this.domImages.nativeElement.querySelectorAll('app-image-thumbnail');
        sortedImages.forEach(sortedImage => {
          if (firstImage) {
            firstImage = false;
          } else {
            imagesOrder += ',';
          }
          // sortedImage.dataset['id'] is obtained using the HTML attribute [attr.data-id]="image.id"
          imagesOrder += sortedImage.dataset['id'];
        });

        console.log('Sorted images ' + imagesOrder);
        this.project.imagesOrdering = imagesOrder;
        console.log('Updating project ' + imagesOrder);
        projectService.saveProject(this.project);
        console.log('Project updated');
        /*
          this.imageThumbnailComponents.forEach((item: ImageThumbnailComponent) =>
            console.log('Image ID ' + item.image.id));*/
      });
  }

  ngOnInit() {
    const routeParams = this.route.snapshot.params;

      this.projectService.getProject$(routeParams.id)
        .subscribe(res =>
          this.project = Object.assign(new Project(), res))
          .add(teardown => {
          this.log('Project component ' + this.project.name + ' with #' + this.project.images.length + ' images');
          this.project.orderImageArray();
      });

      this.projectService.getProjectURLs$(routeParams.id)
        .subscribe(result => this.projectURLs = result).add(teardown => {
        this.log('Project URLs: ' + this.projectURLs);
      });

  }

  /** Log a message with the MessageService */
  private log(message: string) {
    this.messageService.add(`ProjectService: ${message}`);
  }

}
