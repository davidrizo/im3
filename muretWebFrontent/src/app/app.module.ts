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

import { NgxImgModule } from 'ngx-img';
import { UploadImagesComponent } from './upload-images/upload-images.component';

import { FileUploadModule } from 'ng2-file-upload';
import { ImageThumbnailComponent } from './image-thumbnail/image-thumbnail.component';

/* import { LightboxModule } from 'ngx-lightbox';*/


@NgModule({
  declarations: [
    AppComponent,
    ProjectsComponent,
    ProjectComponent,
    StartupComponent,
    AboutComponent,
    ImageComponent,
    NewProjectFormComponent,
    UploadImagesComponent,
    ImageThumbnailComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    UiModule,
    ReactiveFormsModule,
    AngularSvgIconModule,
    FileUploadModule,
    NgxImgModule.forRoot(),
    DragulaModule.forRoot(),
//    LightboxModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
