import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { ProjectsComponent } from './projects/projects.component';

import { HttpClientModule } from '@angular/common/http';
import {AppRoutingModule} from './app-routing.module';
import {UiModule} from './ui/ui.module';

import { ReactiveFormsModule } from '@angular/forms';
import { ProjectComponent } from './project/project.component';
import { StartupComponent } from './startup/startup.component';
import { AboutComponent } from './about/about.component';
import { ImageComponent } from './image/image.component';

import { DragulaModule } from 'ng2-dragula';

import { AngularSvgIconModule } from 'angular-svg-icon';

import {NewProjectFormComponent} from './new-project-form/new-project-form.component';

import { NgxImgModule } from 'ngx-img'; /* I've installed also npm i angular-cropperjs */


@NgModule({
  declarations: [
    AppComponent,
    ProjectsComponent,
    ProjectComponent,
    StartupComponent,
    AboutComponent,
    ImageComponent,
    NewProjectFormComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    ReactiveFormsModule,
    UiModule,
    ReactiveFormsModule,
    AngularSvgIconModule,
    NgxImgModule,
    DragulaModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
