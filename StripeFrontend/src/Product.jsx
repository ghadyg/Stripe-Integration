import React, { useEffect } from 'react'
import {
  Box,
  Button,
  Divider,
  Heading,
  List,
  ListIcon,
  ListItem,
  Stack,
  Text,
  useColorModeValue,
} from '@chakra-ui/react'
import { FaCheckCircle } from 'react-icons/fa'
import { useAuth } from './AuthContext'
import { useNavigate } from 'react-router-dom'

const options = [
  { id: 1, desc: '1 User' },
  { id: 2, desc: 'Basic Support' },
  { id: 3, desc: 'Basic Report'},
]
const options1 = [
  { id: 1, desc: '5 Users' },
  { id: 2, desc: 'Advanced Support' },
  { id: 3, desc: 'Customizable Report'},
]
const options2 = [
  { id: 1, desc: 'Unlimited Users' },
  { id: 2, desc: 'Priority Support' },
  { id: 3, desc: 'Full Reporting'},
]

const links =[
  "https://buy.stripe.com/test_14kaIc5B7fMh98I288",
  "https://buy.stripe.com/test_3cs7w09Rn43z3OoeUV"
] 

const PackageTier = ({ title, options, typePlan,link,customer ,checked = false }) => {
  const colorTextLight = checked ? 'white' : 'purple.600'
  const bgColorLight = checked ? 'purple.400' : 'gray.300'

  const colorTextDark = checked ? 'white' : 'purple.500'
  const bgColorDark = checked ? 'purple.400' : 'gray.300'

  return (
    <Stack
      p={3}
      py={3}
      justifyContent={{
        base: 'flex-start',
        md: 'space-around',
      }}
      direction={{
        base: 'column',
        md: 'row',
      }}
      alignItems={{ md: 'center' }}>
      <Heading size={'md'} width={'75px'}>{title}</Heading>
      <List spacing={3} textAlign="start" width={'200px'}>
        {options.map((desc, id) => (
          <ListItem key={desc.id}>
            <ListIcon as={FaCheckCircle} color="green.500" />
            {desc.desc}
          </ListItem>
        ))}
      </List>
      <Heading size={'xl'} width={'120px'}>{typePlan}</Heading>
      <Stack>
        <Button
          size="md"
          color={useColorModeValue(colorTextLight, colorTextDark)}
          bgColor={useColorModeValue(bgColorLight, bgColorDark)}
          onClick={()=>{
            //console.log(customer);
            window.open(link+`?prefilled_email=${customer}`, '_blank');
            }}>
          Get Started
        </Button>
      </Stack>
    </Stack>
  )
}

export default function Product() {
  const {customer} = useAuth()
  const navigate = useNavigate()
    useEffect(()=>{
    if(!customer){
      navigate("/");
    }
  })
  return (
    <Box py={6} px={5} width="full">
      <Stack spacing={4} width={'100%'} direction={'column'}>
        <Stack
          p={5}
          alignItems={'center'}
          justifyContent={{
            base: 'flex-start',
            md: 'space-around',
          }}
          direction={{
            base: 'column',
            md: 'row',
          }}>
          <Stack
            width={{
              base: '100%',
              md: '40%',
            }}
            textAlign={'center'}>
            <Heading size={'lg'}>
              The Right Plan for <Text color="purple.400">Your Business</Text>
            </Heading>
          </Stack>
          {/* <Stack
            width={{
              base: '100%',
              md: '60%',
            }}>
            <Text textAlign={'center'}>
              Lorem ipsum dolor sit amet consectetur adipisicing elit. Numquam quod in
              iure vero. Facilis magnam, sed officiis commodi labore odit.
            </Text>
          </Stack> */}
        </Stack>
        <Divider />
        <PackageTier title={'Starter'} typePlan="Free" customer={customer?.username} options={options} />
        <Divider />
        <PackageTier
          title={'Growth'}
          checked={true}
          typePlan="$32.00"
          options={options1}
          link={links[0]}
          customer={customer?.username}
        />
        <Divider />
        <PackageTier title={'Scale'} typePlan="$50.00" options={options2} customer={customer?.username} link={links[1]} />
      </Stack>
    </Box>
  )
}
