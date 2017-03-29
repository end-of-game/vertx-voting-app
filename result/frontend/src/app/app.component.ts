import { Component, OnInit } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/toPromise';

import { Vote } from './models/vote';
import { VoteService } from './services/vote.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  public hostname: String = "";
  private choices: String[] = [];
  private myChoice: String = "";
  constructor(private _voteService: VoteService) { }
   
  ngOnInit() {
    this._voteService.getInfo().then((data: Vote) => {
      this.hostname = data.hostname;
      this.choices = data.choice;
    });
  }

  public choose(choice: String): void {
    this.myChoice = choice;
    this._voteService.postChoice(choice);
  }
}
