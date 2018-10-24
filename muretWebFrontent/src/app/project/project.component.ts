import { Component, OnInit } from '@angular/core';
import {ProjectService} from '../projects/project.service';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {Project} from '../model/project';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {
  project: Project;

  constructor(
    private projectService: ProjectService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const routeParams = this.route.snapshot.params;

    this.projectService.getProject$(routeParams.id).
      subscribe(serviceProject => this.project = serviceProject);
  }
}
