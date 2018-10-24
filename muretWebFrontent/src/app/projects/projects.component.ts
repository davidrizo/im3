import { Component, OnInit } from '@angular/core';
import {Project} from '../model/project';
import {ProjectService} from './project.service';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})

export class ProjectsComponent implements OnInit {
  projects: Project[];

  constructor(private projectService: ProjectService) { }

  ngOnInit() {
    this.getProjects();
  }

  getProjects(): void {
    this.projectService.getProjects$().
      subscribe(serviceProjects => this.projects = serviceProjects);
  }
}
