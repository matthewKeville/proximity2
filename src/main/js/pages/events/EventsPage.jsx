import  
  React, { 
} from "react";

import { 
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query';

import EventsTable from "/src/main/js/pages/events/EventsTable.tsx";

const queryClient = new QueryClient()

export default function EventsPage() {
  return (
    <>
      <QueryClientProvider client={queryClient}>
        <EventsTable/>
      </QueryClientProvider>
    </>
  )
}
