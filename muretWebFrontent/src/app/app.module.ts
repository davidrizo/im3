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

@NgModule({
  declarations: [
    AppComponent,
    ProjectsComponent,
    ProjectComponent,
    StartupComponent,
    AboutComponent,
    ImageComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    ReactiveFormsModule,
    UiModule,
    DragulaModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
