import  
  React, { 
} from "react";

import { AppShell, Burger } from '@mantine/core';
import { Text, Space } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { Button } from '@mantine/core';

import NavBar                       from "/src/main/js/components/navbar/NavBar.jsx";
import Header                       from "/src/main/js/components/header/Header.jsx";

import classes from './SplashPage.module.css';

export default function SplashPage() {

    const [opened, { toggle }] = useDisclosure();

    return (

          <AppShell
            header={{ height: 60 }}
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

            <AppShell.Main>

            <div className={classes.container}>
              <div className={classes.titleDiv}> Proximity </div>
              <Space h="xl"/>
              <div className={classes.summaryDiv}> Scrape event data from third party hosts into a common interface </div>
              <Space h="xl"/>
              <ul>
                <li>
                  Create regional event scans for third party hosts.
                </li>
                <li>
                  Browse cross platform events in the event browser.
                </li>
                <li>
                  Automate programmatic RSS feeds, virtual calendars, and more.
                </li>
              </ul>
              <Space h="xl"/>
              <Button variant="filled"  color="green" onClick={() => {window.location.href="/signup"} }>Join Today</Button>
            </div>
                    
            </AppShell.Main>
          </AppShell>
        );
}
