import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CapitalizePipe } from './capitalize.pipe';
import {AppoinmentsService} from "./services/appoinments.service";



@NgModule({
  declarations: [
    CapitalizePipe
  ],
  imports: [
    CommonModule
  ],
  exports:[
    CapitalizePipe
  ],
  providers: [AppoinmentsService]
})
export class SharedModule { }
