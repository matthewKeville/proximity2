/*
This uses Navbar Simple from 
  https://ui.mantine.dev/category/navbars/
as a base. Links are made to use react-router-dom with navigate hook.
*/

import React, { useState } from 'react';
import { Group, Code } from '@mantine/core';
import {
    IconHome,
    IconCalendarEvent,
    IconMap,
    IconBackhoe,
    IconLogout,
} from '@tabler/icons-react';
import { MantineLogo } from '@mantinex/mantine-logo';

import classes from './NavBar.module.css';

import { useNavigate } from "react-router-dom";

const data = [
    { link: '/home', label: 'Home', icon: IconHome },
    { link: '/events', label: 'Events', icon: IconCalendarEvent },
    { link: '/regions', label: 'Regions', icon: IconMap },
    { link: '/compilers', label: 'Compilers', icon: IconBackhoe },
];

export default function NavBar() {

    const [active, setActive] = useState('Home');
    const navigate = useNavigate();

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

            <div className={classes.footer}>
              <a href="#" className={classes.link} onClick={(event) => event.preventDefault()}>
                <IconLogout className={classes.linkIcon} stroke={1.5} />
                <span>Logout</span>
              </a>
            </div>
          </nav>
        );
}
