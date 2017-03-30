import { Component, OnInit } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/toPromise';

import { Result } from './models/result';
import { ResultService } from './services/result.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  private resultVote: Result[];

  constructor(private _resultService: ResultService) { }
  
  ngOnInit() {
    this._resultService.result$.subscribe( (res: Result[]) => {
      this.resultVote = res;
    });
  }
}
