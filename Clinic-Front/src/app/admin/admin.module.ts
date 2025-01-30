import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RoomsComponent } from './rooms/rooms.component';
import { NavbarAdminComponent } from './navbar-admin/navbar-admin.component';
import { SidebarAdminComponent } from './sidebar-admin/sidebar-admin.component';
import {RouterLink, RouterModule} from "@angular/router";
import { DashboardComponent } from './dashboard/dashboard.component';
import { UsersModule } from './users/users.module';
import {AdminRoutingModule} from "./admin-routing.module";
import { EventsModule } from './events/events.module';
import {SharedModule} from "../shared/shared.module";

@NgModule({
  declarations: [
    RoomsComponent,
    NavbarAdminComponent,
    SidebarAdminComponent,
    DashboardComponent,
  ],
  exports: [
    RoomsComponent,
    NavbarAdminComponent,
    SidebarAdminComponent,
    DashboardComponent
  ],
  imports: [
    CommonModule,
    RouterLink,
    RouterModule,
    UsersModule,
    AdminRoutingModule,
    EventsModule,
    SharedModule
  ]
})
export class AdminModule { }
