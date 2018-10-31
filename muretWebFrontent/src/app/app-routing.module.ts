import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProjectsComponent } from './projects/projects.component';
import {ProjectComponent} from './project/project.component';
import {StartupComponent} from './startup/startup.component';
import {AboutComponent} from './about/about.component';
import {ImageComponent} from './image/image.component';
import {NewProjectFormComponent} from './new-project-form/new-project-form.component';
import {UploadImagesComponent} from './upload-images/upload-images.component';
import {AuthGuard} from './auth/auth.guard';
import {LoginComponent} from './auth/login/login.component';

const routes: Routes = [
  { path: 'startup', component: StartupComponent, canActivate: [AuthGuard]},
  { path: 'about', component: AboutComponent },
  { path: 'login', component: LoginComponent},
  { path: 'projects', component: ProjectsComponent, canActivate: [AuthGuard]},
  { path: 'newproject', component: NewProjectFormComponent, canActivate: [AuthGuard]},
  { path: 'project/:id', component: ProjectComponent, canActivate: [AuthGuard]},
  { path: 'image/:id', component: ImageComponent, canActivate: [AuthGuard]},
  { path: 'uploadimages/:id', component: UploadImagesComponent, canActivate: [AuthGuard]},
  { path: '', pathMatch: 'full', redirectTo: 'startup'},
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [ RouterModule.forRoot(routes,
    { enableTracing: false } // <-- debugging purposes only
    ) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
