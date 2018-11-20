import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProjectsComponent } from './projects/projects.component';
import {ProjectComponent} from './project/project.component';
import {StartupComponent} from './startup/startup.component';
import {AboutComponent} from './about/about.component';
import {ImageComponent} from './image/image.component';
import {NewProjectFormComponent} from './new-project-form/new-project-form.component';
import {UploadImagesComponent} from './upload-images/upload-images.component';
import {AuthGuard} from './auth.guard';
import {LoginComponent} from './login/login.component';
import {SymbolsComponent} from './symbols/symbols.component';
import {SVGDrawingComponent} from './svgdrawing/svgdrawing.component';

const routes: Routes = [

  { path: 'svg', component: SVGDrawingComponent }, // PRUEBAS
  { path: 'about', component: AboutComponent },
  { path: 'login', component: LoginComponent},
  { path: 'startup', component: StartupComponent, canActivate: [AuthGuard]},
  { path: 'projects', component: ProjectsComponent, canActivate: [AuthGuard]},
  { path: 'newproject', component: NewProjectFormComponent, canActivate: [AuthGuard]},
  { path: 'project/:id', component: ProjectComponent, canActivate: [AuthGuard], canDeactivate: [AuthGuard]},
//  { path: 'image/:id/:projectURLs', component: ImageComponent, canActivate: [AuthGuard], canDeactivate: [AuthGuard]},
//  { path: 'symbols/:id/:projectURLs', component: SymbolsComponent, canActivate: [AuthGuard], canDeactivate: [AuthGuard]},
  { path: 'image', component: ImageComponent, canActivate: [AuthGuard], canDeactivate: [AuthGuard]},
  { path: 'symbols', component: SymbolsComponent, canActivate: [AuthGuard], canDeactivate: [AuthGuard]},
  { path: 'uploadimages/:id', component: UploadImagesComponent, canActivate: [AuthGuard]},
  // usado para desarrollo //TODO Quitar
  { path: 'dev', component: SymbolsComponent, canDeactivate: [AuthGuard]},
  { path: '', pathMatch: 'full', redirectTo: 'startup'}
  /*{ path: '**', redirectTo: '' },*/
];

@NgModule({
  imports: [ RouterModule.forRoot(routes,
    { enableTracing: false } // <-- debugging purposes only
    ) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
