import { Component, OnInit } from '@angular/core';
import {ProjectService} from '../projects/project.service';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {Project} from '../model/project';
import {MessageService} from '../messages/message.service';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {
  project: Project;

  constructor(
    private projectService: ProjectService,
    private route: ActivatedRoute,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    const routeParams = this.route.snapshot.params;

      this.projectService.getProject$(routeParams.id)
        .subscribe(serviceProject => this.project = serviceProject).add(teardown => {
          this.log('Project component ' + this.project.name + ' with #' + this.project.images.length + ' images');
      });

    // this.log('ProjectComponent: ' + this.project.id + ', ' + this.project.name + ', #images=' + this.project.images.length);
  }

  /** Log a message with the MessageService */
  private log(message: string) {
    this.messageService.add(`ProjectService: ${message}`);
  }

}
