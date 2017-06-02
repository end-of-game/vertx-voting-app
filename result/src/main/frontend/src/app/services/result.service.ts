import { Injectable, OnInit } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs/Rx'

import { Result } from '../models/result';
import { environment } from './../../environments/environment';

declare var vertx: any;
@Injectable()
export class ResultService {

  private BASE_URL_WEBSOCKET: string = environment.API_BASE_URL + '/eventbus';
  private eb: any;

  public result$: BehaviorSubject<any> = new BehaviorSubject<any>('');
  constructor() {
    this.eb = new vertx.EventBus(this.BASE_URL_WEBSOCKET);

      this.eb.onopen = () => {
        this.eb.registerHandler("result", (finalResult) => {
          finalResult = JSON.parse(finalResult);
          let totalVote: number = finalResult.reduce((acc: number, elem: [String, number]) => {
            return acc + elem[1];
          }, 0);

          finalResult = finalResult.map((elem: [String, number]) => {
            return {
              choice: elem[0],
              result: ((elem[1] * 100) / totalVote),
              numberOfVote: elem[1]
            };
          });

          this.result$.next(finalResult);
        });
      }
   }
}
