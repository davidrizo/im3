import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {Im3wsService} from '../im3ws.service';
import {ClassifierType} from '../model/classifier-type';

@Component({
  selector: 'app-preferences',
  templateUrl: './preferences.component.html',
  styleUrls: ['./preferences.component.css']
})
export class PreferencesComponent implements OnInit {

  constructor(private im3WSservice: Im3wsService) {
  }

  classifierTypes: ClassifierType[];
  preferencesGroup = new FormGroup({
    symbolImageClassifiersSelect: new FormControl()
  });

  onSubmit() {
  }

  ngOnInit(): void {
    this.im3WSservice.getClassifierTypes$().subscribe(next => {
      this.classifierTypes = next;
    });
  }

}
