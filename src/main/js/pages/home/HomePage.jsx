import  
  React, { 
} from "react";

import { AppShell, Burger } from '@mantine/core';
import { Text, Space } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';

import NavBar                       from "/src/main/js/components/navbar/NavBar.jsx";
import Header                       from "/src/main/js/components/header/Header.jsx";

import classes from './HomePage.module.css';

export default function HomePage() {

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
              <div className={classes.titleDiv}> Unified Event Discovery</div>
              <Space h="xl"/>
              <div className={classes.summaryDiv}> Scrape event data from third party hosts into a common interface </div>
            </div>
                    
            </AppShell.Main>
          </AppShell>
        );
}
