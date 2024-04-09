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

type Location = {
  name: string;
  country: string;
  region: string;
  locality: string;
  streetAddress: string;
  latitude: number;
  longitude: number;
}

type Event = {
  id: number;
  name: string;
  eventType: string;
  description: string;
  start: string;
  isVirtual: boolean;
  url: string;
  location: Location;
}

const colSizeBase = 180;

function getEventTypeImage(row) {

  switch(row.eventType) {
    case "EVENTBRITE":
      return "/favicons/eventbrite-favicon.ico";
    case "MEETUP":
      return "/favicons/meetup-favicon.ico";
    case "DEV":
    defafult:
      return "/favicons/favicon.ico";
  }

}

function getDateDisplay(row) {

  //let date: Date = new Date(Date.parse(row.start))
  let date: Date = new Date(row.start)

  let weekday = ""

  switch(date.getDay()) {
    case 0:
      weekday = "Sunday"
      break;
    case 1:
      weekday = "Monday"
      break;
    case 2:
      weekday = "Tuesday"
      break;
    case 3:
      weekday = "Wednesday"
      break;
    case 4:
      weekday = "Thursday"
      break;
    case 5:
      weekday = "Friday"
      break;
    case 6:
      weekday = "Saturday"
      break;
  }

  let shortTime = ""

  if ( date.getHours() > 12 ) { 
    shortTime = date.getHours()-12 + ":" + date.getMinutes() + " PM";
  } else {
    shortTime = date.getHours() + ":" + date.getMinutes() + " AM";
  }

  //return weekday + " " + date.getDate() + " " + shortTime;
  return `${weekday} ${date.getMonth()}/${date.getDate()} ${shortTime}`

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
        accessorKey: 'eventType',
        header: "",
        Cell: ( { cell, column } ) => (
          <Box>
            <Image h={16} w={16} src={
              getEventTypeImage(cell.row.original)
            } />
          </Box>
        ),
        size: colSizeBase / 8
      },
      {
        accessorKey: 'name',
        header: "Name"
      },
      {
        id: 'description',
        accessorFn: (row) => { 
          return row.description.substr(0,Math.min(row.description.length,60))
        } ,
        header: "Description"
      },
      {
        accessorKey: 'start',
        header: "Start*",
        Cell: ( { cell, column } ) => (
          <div>
          {getDateDisplay(cell.row.original)}
          </div>
        )
      },
      {
        accessorKey: 'location',
        header: "Location",
        Cell: ( { cell, column } ) => (
          <div>
          { 
            cell.row.original.isVirtual ? 
              <span> Online</span> 
            : 
              <span> {cell.row.original.location.name} : {cell.row.original.location.locality} , {cell.row.original.location.region} </span> 
          }
          </div>
        )
      },
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

