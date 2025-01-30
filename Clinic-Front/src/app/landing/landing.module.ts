import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LandingComponent } from './landing/landing.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {SharedModule} from "../shared/shared.module";
import { PublicNavbarComponent } from './public-navbar/public-navbar.component';
import { ImageCarouselComponent } from './image-carousel/image-carousel.component';
import { DoctorReservationComponent } from './doctor-reservation/doctor-reservation.component';



@NgModule({
  declarations: [
    LandingComponent,
    PublicNavbarComponent,
    ImageCarouselComponent,
    DoctorReservationComponent
  ],
  exports: [
    LandingComponent,
    DoctorReservationComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ReactiveFormsModule
  ]
})
export class LandingModule { }
