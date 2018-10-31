import { Component, OnInit } from '@angular/core';
import {Project} from '../model/project';
import {Im3wsService} from '../im3ws.service';
import {NGXLogger} from 'ngx-logger';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})

export class ProjectsComponent implements OnInit {
  projects: Project[];

  constructor(private projectService: Im3wsService, private logger: NGXLogger) { }

  ngOnInit() {
    this.getProjects();
  }

  getProjects(): void {
    this.logger.debug('Obtaining projects');
    this.projectService.getProjects$().
      subscribe(serviceProjects => this.projects = serviceProjects);
  }
}
