import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DoctorReservationComponent } from './doctor-reservation.component';

describe('DoctorReservationComponent', () => {
  let component: DoctorReservationComponent;
  let fixture: ComponentFixture<DoctorReservationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DoctorReservationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DoctorReservationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
