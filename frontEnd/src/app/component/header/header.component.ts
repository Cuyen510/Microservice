import { AfterViewInit, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import 'bootstrap/dist/css/bootstrap.min.css';
import * as bootstrap from 'bootstrap';


@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  standalone: true,
  imports: [    
    CommonModule,
    RouterModule
  ]
})
export class HeaderComponent implements AfterViewInit {
  ngAfterViewInit(): void {
    const dropdownElementList = document.querySelectorAll('.dropdown-toggle');
    dropdownElementList.forEach(dropdownEl => {
      new bootstrap.Dropdown(dropdownEl);
    });
  }

  

}
