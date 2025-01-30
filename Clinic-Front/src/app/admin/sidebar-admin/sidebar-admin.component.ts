import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-sidebar-admin',
  templateUrl: './sidebar-admin.component.html',
  styleUrl: './sidebar-admin.component.scss'
})
export class SidebarAdminComponent {
  @Input() isOpen = true;
  @Input() menuItems: any[] = [];
  @Input() user:any
  activeDropdown: string | null = null;

  toggleDropdown(title: string): void {
    this.activeDropdown = this.activeDropdown === title ? null : title;
  }
}
