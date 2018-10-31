import { BrowserModule } from '@angular/platform-browser';
import {Injectable, NgModule} from '@angular/core';

import { AppComponent } from './app.component';
import { ProjectsComponent } from './projects/projects.component';

import {HTTP_INTERCEPTORS, HttpClientModule, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
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

import { FormsModule } from '@angular/forms';

/* import { LightboxModule } from 'ngx-lightbox';*/
// Auth
import {
  OktaAuthModule,
  OktaCallbackComponent,
} from '@okta/okta-angular';
import {RouterModule} from '@angular/router';
import {Im3wsService} from './im3ws.service';
import {LoginComponent} from './login/login.component';

import {LoggerModule, NGXLogger, NgxLoggerLevel} from 'ngx-logger';

// TODO Ver Keepass
/*const config = {
  issuer: 'https://dev-775794.oktapreview.com/oauth2/default',
  redirectUri: 'http://localhost:{port}/implicit/callback',
  clientId: '{clientId}'
}*/

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
    ImageThumbnailComponent,
    LoginComponent
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
    FormsModule,
    RouterModule,
    LoggerModule.forRoot({serverLoggingUrl: '/api/logs', level: NgxLoggerLevel.DEBUG, serverLogLevel: NgxLoggerLevel.ERROR})
    //    LightboxModule
  ],
  providers: [Im3wsService, NGXLogger], // singleton
  bootstrap: [AppComponent]
})
export class AppModule { }

