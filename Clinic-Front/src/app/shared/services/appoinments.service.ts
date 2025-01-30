import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {BehaviorSubject, map, Observable} from "rxjs";
import {EventDTO} from "../../admin/events/events.service";

@Injectable({
  providedIn: 'root'
})
export class AppoinmentsService {
  private baseUrl = environment.apiBaseUrl + '/appointments';
  private selectedSpecialty = new BehaviorSubject<string>('all');

  constructor(private http: HttpClient,private router: Router) { }

  getAppointments(): Observable<any> {
    return this.http.get<any>(this.baseUrl);
  }

  getAllDoctorsAvailability(start: string, end: string) {
    return this.http.get<any[]>(`${this.baseUrl}/available-all`, {
      params: { start, end }
    });
  }

  getDoctorsAvailabilityBySpecialty(specialty: string, start: string, end: string) {
    return this.http.get<any[]>(`/available-by-specialty`, {
      params: { specialty, start, end }
    });
  }

  setSelectedSpecialty(specialty: string) {
    this.selectedSpecialty.next(specialty);
  }

  getSelectedSpecialty() {
    return this.selectedSpecialty.asObservable();
  }
}
