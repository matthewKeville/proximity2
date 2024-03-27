import React, { useState, useEffect } from 'react';
import {
    IconCalendarEvent,
    IconMap,
    IconBackhoe,
} from '@tabler/icons-react';

import classes from './NavBar.module.css';

import { useNavigate } from "react-router-dom";

const data = [
    { link: '/events', label: 'Events', icon: IconCalendarEvent },
    { link: '/regions', label: 'Regions', icon: IconMap },
    { link: '/compilers', label: 'Compilers', icon: IconBackhoe }
];

export default function NavBar() {

    const [active, setActive] = useState('Events');
    const navigate = useNavigate();

    const resolvePage = () => {
      let path = window.location.pathname
      let active = data.filter( page => page.link == path )
      if (active.length > 0) {
        setActive(active[0].label)
      } else {
        setActive('Events')
      }
    }

    useEffect( 
      () => {
        resolvePage()
      },
      []
    );

    const links = data.map((item) => (
          <a
            className={classes.link}
            data-active={item.label === active || undefined}
            href={item.link}
            key={item.label}
            onClick={(event) => {
                      event.preventDefault();
                      setActive(item.label);
                      navigate(item.link);
                    }}
          >
            <item.icon className={classes.linkIcon} stroke={1.5} />
            <span>{item.label}</span>
          </a>
        ));

    return (

          <nav className={classes.navbar}>
            <div className={classes.navbarMain}>
              {links}
            </div>
          </nav>
        );
}
