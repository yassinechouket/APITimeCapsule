import { Component,Output,EventEmitter, OnInit, HostListener } from '@angular/core';
import{navbarData} from './nav-data';
import { RouterModule } from '@angular/router';
import{CommonModule} from '@angular/common';
import{trigger,transition,style,animate,keyframes} from '@angular/animations';
import{SublevelMenu} from './sublevel-menu';
import { INavbarData } from './helper';


interface sideNavToggle{
  screeWidth:number;
  collapsed:boolean;
}
@Component({
  selector: 'app-sidenav',
  imports: [CommonModule, RouterModule, SublevelMenu],
  templateUrl: './sidenav.html',
  styleUrls: ['./sidenav.scss'],

  animations: [
    trigger('fadeInOut', [
      transition(':enter', [
        style({opacity: 0}),
        animate('350ms',
          style({opacity: 1}))
      ]),
      transition(':leave', [
        style({opacity: 1}),
        animate('350ms',
          style({opacity: 0}))
      ])
    ]),
    trigger('rotate', [
      transition(':enter', [
        animate('1000ms',
          keyframes([
            style({transform: 'rotate(0deg)', offset: 0}),
            style({transform: 'rotate(180deg)', offset: 1})
          ]))
      ])
    ])
  ]
})
export class Sidenav implements OnInit {
  @Output() onToggleSideNav : EventEmitter<sideNavToggle>=new EventEmitter();
  collapsed=false;
  screeWidth=0;
  navData=navbarData;
  multiple:boolean=false;

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
  this.screeWidth = window.innerWidth;
  if(this.screeWidth <= 768) {
    this.collapsed = false;
    this.onToggleSideNav.emit({collapsed: this.collapsed, screeWidth: this.screeWidth});
    }
  }
  ngOnInit():void{
    this.screeWidth=window.innerWidth;
  }
  

  toggleCollapse():void{
    this.collapsed=!this.collapsed;
    this.onToggleSideNav.emit({collapsed:this.collapsed,screeWidth:this.screeWidth});
  }
  closeSidenav():void{
    this.collapsed=false;
    this.onToggleSideNav.emit({collapsed:this.collapsed,screeWidth:this.screeWidth});
  }

  handleClick(item: INavbarData): void {
    if (!this.multiple) {
      for(let modelItem of this.navData) {
        if (item !== modelItem && modelItem.expanded) {
          modelItem.expanded = false;
        }
      }
    }
    item.expanded = !item.expanded;
  }



}
