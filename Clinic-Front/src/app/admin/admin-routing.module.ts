import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminDashboardComponent } from '../core/pages/admin-dashboard/admin-dashboard.component';
import { AuthGuard } from '../core/pages/guards/auth.guard';
import { RoleGuard } from '../core/pages/guards/role.guard';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UsersListComponent } from './users/users-list/users-list.component';
import { RoomsComponent } from './rooms/rooms.component';

const routes: Routes = [
  {
    path: '',
    component: AdminDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'DOCTOR'] },
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'users', component: UsersListComponent, data: { roles: ['ADMIN','DOCTOR'] } },
      { path: 'rooms', component: RoomsComponent },
      { path: 'admin/events',loadChildren: () =>import('./events/events.module').then((m) => m.EventsModule)},
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule {}
