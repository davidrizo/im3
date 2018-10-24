import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProjectsComponent } from './projects/projects.component';
import {ProjectComponent} from './project/project.component';
import {StartupComponent} from './startup/startup.component';
import {AboutComponent} from './about/about.component';
import {ImageComponent} from './image/image.component';

const routes: Routes = [
  { path: 'startup', component: StartupComponent },
  { path: 'about', component: AboutComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'newproject', component: ProjectComponent },
  { path: 'project/:id', component: ProjectComponent },
  { path: 'image/:id', component: ImageComponent }
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
