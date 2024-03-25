import  
  React, { 
} from "react";

import { 
  Outlet
} from "react-router-dom";

import { AppShell, Burger } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';

import NavBar                       from "/src/main/js/components/navbar/NavBar.jsx";
import Header                       from "/src/main/js/components/header/Header.jsx";

import classes                      from "./Root.module.css"

/*
export default function Root() {
  return (
    <>
    <Header className="header"/>
    <NavBar className="nav"/>
    <Outlet className="page"/>
    </>
  )
}
*/


export default function Root() {
    const [opened, { toggle }] = useDisclosure();

    return (
          <AppShell
            header={{ height: 60 }}
            navbar={{
                      width: 300,
                        breakpoint: 'sm',
                        collapsed: { mobile: !opened },
                      }}
            padding="md"
          >
            <AppShell.Header>
              <Burger
                opened={opened}
                onClick={toggle}
                hiddenFrom="sm"
                size="sm"
              />
              <Header/>
            </AppShell.Header>

            <AppShell.Navbar p="md"><NavBar/></AppShell.Navbar>

            <AppShell.Main><Outlet/></AppShell.Main>
          </AppShell>
        );
}
