import  
  React, {
  useMemo, 
} from "react";

import {
  ActionIcon,
  Flex,
  Tooltip,
  Box,
  Image,
} from '@mantine/core';

import {
  ModalsProvider, 
} from "@mantine/modals";

import { IconTrash, IconExternalLink } from '@tabler/icons-react';

import { 
  MantineReactTable, 
  useMantineReactTable,
} from "mantine-react-table";

import 'mantine-react-table/styles.css';  //make sure MRT styles were imported in your app root (once)

import { 
  useQuery,
} from '@tanstack/react-query';

type Event = {
  id: number;
  name: string;
  eventType: string;
  description: string;
  start: string;
  isVirtual: boolean; //boolean?
  url: string; //boolean?
}


function getEventTypeImage(row) {

  switch(row.eventType) {
    case "DEV":
      return "/favicons/eventbrite-favicon.ico";
    defafult:
      return "/favicons/favicon.ico";
  }

}


function useGetEvents() {
  return useQuery<Event[]>({
    queryKey: ['events'],
    queryFn: async () =>  {
      await new Promise((resolve) => setTimeout(resolve,300));
      return Promise.resolve(fetch("/api/events").then( (res) => res.json() ));
    }
  })
}

export default function EventsTable() {

  //fetch Region
  const {
    data: fetchedEvents = [],
    isError : isLoadingEventsError,
    isLoading : isLoadingEvents
  } = useGetEvents()

  const columns = useMemo(
    () => [
      {
        accessorKey: 'name',
        header: "Name"
      },
      {
        accessorKey: 'eventType',
        header: "Type",
        Cell: ( { cell, column } ) => (
          <Box>
            <Image h={24} w={24} src={
              getEventTypeImage(cell.row.original)
            } />
          </Box>
        )
      },
      {
        accessorKey: 'description',
        header: "Desc"
      },
      {
        accessorKey: 'start',
        header: "Start"
      },
      {
        //accessorKey: 'isVirtual',
        /*
        */
        id: 'isVirtual',
        accessorFn: (row) => { 
          return row.isVirtual ? " Virtual " : " Physical "
        } ,
        header: "Environment"
      }
    ],
    []
  );

  const table = useMantineReactTable({
    columns,
    data: fetchedEvents,
    enableRowActions: true,
    positionActionsColumn: "last",
    renderRowActions: ({ row, table }) => (
      <Flex gap="md">
        <Tooltip label="To Source">
          <ActionIcon color="red" onClick={() => { window.open(row.original.url) } }>
            <IconExternalLink />
          </ActionIcon>
        </Tooltip>
      </Flex>
    )

  });

  if (isLoadingEvents) return "Loading..."
  if (isLoadingEventsError) return "An error occurred"

  return (
    <ModalsProvider>
      <MantineReactTable table={table} />
    </ModalsProvider>
  )

}

