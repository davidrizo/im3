import { Component, OnInit } from '@angular/core';
import { FileUploader } from 'ng2-file-upload';
import {ActivatedRoute} from '@angular/router';
import {Project} from '../model/project';
import {Im3wsService} from '../im3ws.service';
import {NGXLogger} from 'ngx-logger';

const URL = 'http://localhost:8080/muret/upload/projectImage';

@Component({
  selector: 'app-upload-images',
  templateUrl: './upload-images.component.html',
  styleUrls: ['./upload-images.component.css']
})
export class UploadImagesComponent implements OnInit {
  public project: Project;
  public uploader: FileUploader = new FileUploader({url: URL});
  public hasBaseDropZoneOver = true;
  public hasAnotherDropZoneOver = false;

  constructor(private projectService: Im3wsService, private route: ActivatedRoute, private logger: NGXLogger) {
  }

  /* TODO Intentar pasar el objeto project directamente con redux? */
  ngOnInit() {
    const routeParams = this.route.snapshot.params;

    this.projectService.getProject$(routeParams.id)
      .subscribe(serviceProject => this.project = serviceProject).add(teardown => {
      this.logger.debug('UploadImagesComponent' + this.project.name + ' with #' + this.project.images.length + ' images');
    });

    // it avoids CORS problems
    this.uploader.onBeforeUploadItem = (item) => {
      item.withCredentials = false;
    };
    this.uploader.onAfterAddingFile = (file) => { file.withCredentials = false; };

    // Add in the other upload form parameters.
    this.uploader.onBuildItemForm = (item, form) => {
      form.append('projectid', this.project.id);
    };
  }

  public fileOverBase(e: any): void {
    this.hasBaseDropZoneOver = e;
  }

  public fileOverAnother(e: any): void {
    this.hasAnotherDropZoneOver = e;
  }
}
