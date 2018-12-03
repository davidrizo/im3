import { Injectable } from '@angular/core';
import {Project} from './model/project';
import {ComponentCanDeactivate} from './component-can-deactivate';
import {Image} from './model/image';

@Injectable({
  providedIn: 'root'
})

export class SessionDataService {
  private _currentProject: Project;
  private _currentImage: Image;
  private _currentImageMastersURL: string;

  get currentProject(): Project {
    return this._currentProject;
  }

  set currentProject(value: Project) {
    this._currentProject = value;
  }

  get currentImage(): Image {
    return this._currentImage;
  }

  set currentImage(value: Image) {
    this._currentImage = value;
  }

  get currentImageMastersURL(): string {
    return this._currentImageMastersURL;
  }

  set currentImageMastersURL(value: string) {
    this._currentImageMastersURL = value;
  }
}
