import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../auth/services/auth.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  userRole: string | null = null;
  isSidebarOpen = true;
  clinicName = 'ClinicPro Health Center';
  doctorName = 'Dr. Smith';
  todayPatients = 24;
  pendingAppointments = 8;
  pendingReports = 5;
  menuItems = [
    {
      title: 'Dashboard',
      icon: 'chart-line',
      path: '/admin/dashboard'
    },
    {
      title: 'Patient Management',
      icon: 'users',
      submenu: [
        { title: 'Patient List', path: '/admin/patients' },
        { title: 'Add Patient', path: '/admin/patients/new' },
        { title: 'Medical Records', path: '/admin/records' }
      ]
    },
    {
      title: 'Appointments',
      icon: 'calendar-alt',
      submenu: [
        { title: 'Schedule', path: '/admin/appointments' },
        { title: 'Calendar View', path: '/admin/calendar' }
      ]
    },
    {
      title: 'Prescriptions',
      icon: 'prescription',
      path: '/admin/prescriptions'
    },
    {
      title: 'Lab Reports',
      icon: 'flask',
      path: '/admin/lab-reports'
    },
    {
      title: 'Billing',
      icon: 'file-invoice-dollar',
      submenu: [
        { title: 'Invoices', path: '/admin/billing/invoices' },
        { title: 'Payments', path: '/admin/billing/payments' }
      ]
    },
    {
      title: 'Settings',
      icon: 'cog',
      path: '/admin/settings'
    }
  ];
  user:any

  constructor(private authService: AuthService) {
    //this.userRole = this.authService.getUserRole(); // e.g. 'ADMIN', 'MANAGER'
    this.user = this.authService.getUser();
  }

  ngOnInit() {
    this.userRole = this.authService.getUserRole();
  }

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  goToProfile() {}

  logout() {
    this.authService.logout();
  }
}
