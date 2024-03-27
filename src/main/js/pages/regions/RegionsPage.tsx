import  
  React, 
  { 
    useMemo, 
    useState 
} from "react";

import {
  ActionIcon,
  Button,
  Flex,
  Stack,
  Text,
  Title,
  Tooltip,
} from '@mantine/core';

import {
  ModalsProvider, 
  modals 
} from "@mantine/modals";

import { IconEdit, IconTrash } from '@tabler/icons-react';

import { 
  MRT_EditActionButtons,
  MantineReactTable, 
  useMantineReactTable,
  MRT_TableOptions,
} from "mantine-react-table";

import 'mantine-react-table/styles.css';  //make sure MRT styles were imported in your app root (once)

import { 
  QueryClient,
  QueryClientProvider,
  useQuery,
  useMutation,
  useQueryClient
} from '@tanstack/react-query'

import { ReactQueryDevtools } from '@tanstack/react-query-devtools'

type Region = {
  id: number;
  name: string;
  radius: number;
  latitude: number;
  longitude: number;
}

const queryClient = new QueryClient()

function useGetRegions() {
  return useQuery<Region[]>({
    queryKey: ['regions'],
    queryFn: async () =>  {
      await new Promise((resolve) => setTimeout(resolve,300));
      return Promise.resolve(fetch("/api/regions").then( (res) => res.json() ));
    }
  })
}

function useCreateRegion() {

  const queryClient = useQueryClient();

  return useMutation({

    mutationFn: async (region: Region) => {
      fetch("/api/regions", {
        method: "POST",
        body: JSON.stringify({
          name: region.name,
          radius: region.radius,
          latitude: region.latitude,
          longitude: region.longitude
        }),
        headers: {
          "Content-type": "application/json; charset=UTF-8"
        }
      })
      .then((response) => response.json());
    },

    onSuccess: async () => await queryClient.invalidateQueries({ queryKey: ['regions'] }),

  });
}

function useDeleteRegion() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (regionId: string) => {
      fetch(`/api/regions/${regionId}`, {
        method: "DELETE",
        headers: {
          "Content-type": "application/json; charset=UTF-8"
        }
      })
    },
    /* client side optimistic delete (remove region to be deleted from table) */
    onMutate: (regionId: string) => {
      queryClient.setQueryData(['regions'], (prevRegions: any) =>
        prevRegions?.filter( (region: Region) => region.id.toString() !=  regionId));
    },
    onSuccess: async () => await queryClient.invalidateQueries({ queryKey: ['regions'] }),

  });
}

function RegionsTable() {

  const [validationErrors, setValidationErrors] = useState<
    Record<string, string | undefined> >({});

  //fetch Region
  const {
    data: fetchedRegions = [],
    isError : isLoadingRegionsError,
    isFetching : isFetchingRegions,
    isLoading : isLoadingRegions
  } = useGetRegions()

  //create Region
  const { 
    mutateAsync: createRegion, 
    isPending: isCreatingRegion 
  } = useCreateRegion()

  //delete Region
  const { 
    mutateAsync: deleteRegion, 
    isPending: isDeletingRegion 
  } = useDeleteRegion();


  //table triggers

  const handleCreateRegion: MRT_TableOptions<Region>['onCreatingRowSave'] = async ({
    values,
    exitCreatingMode,
  }) => {
    const newValidationErrors = validateRegion(values);
    if (Object.values(newValidationErrors).some((error) => error)) {
      setValidationErrors(newValidationErrors);
      return;
    }
    setValidationErrors({});
    await createRegion(values);
    exitCreatingMode(); 
  }
  
  const openDeleteConfirmModal = (row: MRT_Row<Region>) =>
    modals.openConfirmModal({
      title: 'Are you sure you want to delete this region?',
      children: (
        <Text>
          Are you sure you want to delete {row.original.name}{' '}?
          This action cannot be undone.
        </Text>
      ),
      labels: { confirm: 'Delete', cancel: 'Cancel' },
      confirmProps: { color: 'red' },
      onConfirm: () => deleteRegion(row.original.id),
    });

  const columns = useMemo(
    () => [
      {
        accessorKey: 'id',
        header: "ID",
        //It is not clear from the docs how to hide fields in the edit/create modal. This appears to work
        //https://stackoverflow.com/questions/76869302/is-there-a-way-to-disable-or-hide-fields-of-create-new-item-modal-of-material
        Edit: () => null,
      },
      {
        accessorKey: 'name',
        header: "Name"
      },
      {
        accessorKey: 'radius',
        header: "Radius"
      },
      {
        accessorKey: 'latitude',
        header: "Latitude"
      },
      {
        accessorKey: 'longitude',
        header: "Longitude"
      }
    ],
    []
  );

  const table = useMantineReactTable({
    columns,
    data: fetchedRegions,
    onCreatingRowSave: handleCreateRegion,
    createDisplayMode: 'modal',

    renderCreateRowModalContent: ({ table, row, internalEditComponents }) => (
     <Stack>
        <Title order={3}>Create new Region</Title>
        {/*{internalEditComponents}*/}
        {internalEditComponents}
        <Flex justify="flex-end" mt="xl">
          <MRT_EditActionButtons variant="text" table={table} row={row} />
        </Flex>
      </Stack>

    ),

    renderTopToolbarCustomActions: ({ table }) => (
      <Button onClick={() => { table.setCreatingRow(true); }} >
        Create New Region
      </Button>
    ),

    enableRowActions: true,

    renderRowActions: ({ row, table }) => (
      <Flex gap="md">
        <Tooltip label="Delete">
          <ActionIcon color="red" onClick={() => openDeleteConfirmModal(row)}>
            <IconTrash />
          </ActionIcon>
        </Tooltip>
      </Flex>
    )

  });

  if (isLoadingRegions) return "Loading..."
  if (isLoadingRegionsError) return "An error occurred"

  return (
    <MantineReactTable table={table} />
  )

}

function validateRegion(region: Region) {
  return {
    name: false //placeholder 
      ? 'name is Required'
      : '',
  };
}


export default function RegionsPage() {

  return (
    <QueryClientProvider client={queryClient}>
      <ModalsProvider> {/* can suppress with children={{}}*/}
        <RegionsTable />
      </ModalsProvider>
      {/*<ReactQueryDevtools initialIsOpen={true} />*/}
    </QueryClientProvider>
  )

}
