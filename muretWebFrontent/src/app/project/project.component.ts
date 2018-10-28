import { Component, OnInit } from '@angular/core';
import {ProjectService} from '../projects/project.service';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {Project} from '../model/project';
import {MessageService} from '../messages/message.service';
import {ConfigurationService} from '../configuration.service';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {
  project: Project;
  thumbnailURL = 'http://localhost:8888/muret/prueba-2/thumbnails';

  constructor(
    private projectService: ProjectService,
    private route: ActivatedRoute,
    private messageService: MessageService,
    private configurationService: ConfigurationService
  ) {}

  ngOnInit() {
    const routeParams = this.route.snapshot.params;

      this.projectService.getProject$(routeParams.id)
        .subscribe(serviceProject => this.project = serviceProject).add(teardown => {
          this.log('Project component ' + this.project.name + ' with #' + this.project.images.length + ' images');
      });

      this.projectService.getThumbnailsURL$(routeParams.id)
        .subscribe(result => this.thumbnailURL = result.response).add(teardown => {
        this.log('Project thumbnails URL: ' + this.thumbnailURL);
      });
  }

  /** Log a message with the MessageService */
  private log(message: string) {
    this.messageService.add(`ProjectService: ${message}`);
  }

}
