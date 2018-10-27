import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProjectsComponent } from './projects/projects.component';
import {ProjectComponent} from './project/project.component';
import {StartupComponent} from './startup/startup.component';
import {AboutComponent} from './about/about.component';
import {ImageComponent} from './image/image.component';
import {NewProjectFormComponent} from './new-project-form/new-project-form.component';
import {UploadImagesComponent} from './upload-images/upload-images.component';

const routes: Routes = [
  { path: 'startup', component: StartupComponent },
  { path: 'about', component: AboutComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'newproject', component: NewProjectFormComponent },
  { path: 'project/:id', component: ProjectComponent },
  { path: 'image/:id', component: ImageComponent },
  { path: 'uploadimages/:id', component: UploadImagesComponent },
];

@NgModule({
  imports: [ RouterModule.forRoot(routes,
    { enableTracing: false } // <-- debugging purposes only
    ) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
