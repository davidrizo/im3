import { Component, OnInit } from '@angular/core';
import {Project} from '../model/project';
import {Im3wsService} from '../im3ws.service';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})

export class ProjectsComponent implements OnInit {
  projects: Project[];

  constructor(private projectService: Im3wsService) { }

  ngOnInit() {
    this.projectService.checkAuthorized();
    this.getProjects();
  }

  getProjects(): void {
    this.projectService.getProjects$().
      subscribe(serviceProjects => this.projects = serviceProjects);
  }
}
