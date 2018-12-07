import { Component, OnInit } from '@angular/core';
import {Project} from '../model/project';
import {Im3wsService} from '../im3ws.service';
import {NGXLogger} from 'ngx-logger';
import {Permission} from '../model/permission';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})

export class ProjectsComponent implements OnInit {
  myProjects: Array<Project>;
  permissions: Array<Permission>;

  constructor(private im3wsService: Im3wsService, private logger: NGXLogger) { }

  ngOnInit() {
    // this.getProjects();
    this.myProjects = this.im3wsService.getUser().projectsCreated;
    this.permissions = this.im3wsService.getUser().permissions;

    /*this.myProjects = new Array<Project>();
    this.im3wsService.getUser().projectsCreated.forEach(project => {
      const target: Project = Object.assign(new Project(), project);
      this.myProjects.push(target);
    });
    this.myProjects = this.im3wsService.getUser().projectsCreated;

    this.permissions = new Array<Permission>();
    this.im3wsService.getUser().permissions.forEach(permission => {
      const target: Permission = Object.assign(new Permission(), permission);
      this.permissions.push(target);
    });*/
  }

  /*getProjects(): void {
    this.logger.debug('Obtaining projects');
    this.im3wsService.getProjects$().
      subscribe(serviceProjects => this.projects = serviceProjects);
  }*/
}
