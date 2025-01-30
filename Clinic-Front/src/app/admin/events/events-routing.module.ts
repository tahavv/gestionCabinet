import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {EventsListComponent} from "./events-list/events-list.component";
import {EventFormComponent} from "./event-form/event-form.component";
import {CalendarComponent} from "./calendar/calendar.component";
import {EventDetailComponent} from "./event-detail/event-detail.component";


// If you want role-based guard: e.g. canActivate: [RoleGuard], data: { roles: ['ADMIN','MANAGER'] }
const routes: Routes = [
  { path: '', component: EventsListComponent },
  { path: 'create', component: EventFormComponent, data: { isEdit: false } },
  { path: 'edit/:id', component: EventFormComponent, data: { isEdit: true } },
  { path: 'calendar', component: CalendarComponent },
  { path: 'detail/:id', component: EventDetailComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EventsRoutingModule {}
