import React, { useRef } from 'react'
import {
  Flex,
  Box,
  FormControl,
  FormLabel,
  Input,
  InputGroup,
  HStack,
  InputRightElement,
  Stack,
  Button,
  Heading,
  Text,
  useColorModeValue,
  Link,
} from '@chakra-ui/react'
import { useState } from 'react'
import { ViewIcon, ViewOffIcon,CheckCircleIcon} from '@chakra-ui/icons'
import { errorNotification } from './notification'
import { signup } from './client'
import './Signup.css';


export default function Signup() {
  const [showPassword, setShowPassword] = useState(false)
  const [credentials, setCredentials] = useState({
    username: "",
    password :"",
    confirmPassword:"",
  })

  const dialogRef = useRef(null);

  return (
    <Flex
      minH={'100vh'}
      align={'center'}
      justify={'center'}
      bg={useColorModeValue('gray.50', 'gray.800')}>
      <Stack spacing={8} mx={'auto'} maxW={'lg'} py={12} px={6}>
        <Stack align={'center'}>
          <Heading fontSize={'4xl'} textAlign={'center'}>
            Sign up
          </Heading>
          <Text fontSize={'lg'} color={'gray.600'}>
            to enjoy all of our cool Products ✌️
          </Text>
        </Stack>
        <Box
          rounded={'lg'}
          bg={useColorModeValue('white', 'gray.700')}
          boxShadow={'lg'}
          p={8}>
          <Stack spacing={4}>
            <FormControl id="Text" isRequired>
              <FormLabel>Username</FormLabel>
              <Input type="text" value={credentials.username} onChange={(event)=>{
                setCredentials({
                  ...credentials,
                  username: event.target.value,
                });
              }} />
            </FormControl>
            <FormControl id="password" isRequired>
              <FormLabel>Password</FormLabel>
              <InputGroup>
                <Input type={showPassword ? 'text' : 'password'} value={credentials.password} onChange={(event)=>{
                setCredentials({
                  ...credentials,
                  password: event.target.value,
                });
              }} />
                <InputRightElement h={'full'}>
                  <Button
                    variant={'ghost'}
                    onClick={() => setShowPassword((showPassword) => !showPassword)}>
                    {showPassword ? <ViewIcon /> : <ViewOffIcon />}
                  </Button>
                </InputRightElement>
              </InputGroup>
            </FormControl>
            <FormControl id="confirmPassword" isRequired>
              <FormLabel>Confirm Password</FormLabel>
              <InputGroup>
                <Input type={'password'} value={credentials.confirmPassword} onChange={(event)=>{
                setCredentials({
                  ...credentials,
                  confirmPassword: event.target.value,
                });
              }}/>
                <InputRightElement h={'full'}>
                </InputRightElement>
              </InputGroup>
            </FormControl>
            <Stack spacing={10} pt={2}>
              <Button
                onClick={()=>{
                  if(credentials.confirmPassword !== credentials.password)
                  {
                    errorNotification("Not Match","Passwords doesnt match!");
                    return
                  }
                  signup({username:credentials.username,password:credentials.password}).then(()=>{
                    dialogRef.current.showModal();
                  }).catch((err)=>errorNotification(err))
                }}
                loadingText="Submitting"
                size="lg"
                bg={'blue.400'}
                color={'white'}
                _hover={{
                  bg: 'blue.500',
                }}>
                Sign up
              </Button>
            </Stack>
            <Stack pt={6}>
              <Text align={'center'}>
                Already a user? <Link href='/' color={'blue.400'}>Login</Link>
              </Text>
            </Stack>
          </Stack>
        </Box>
      </Stack>
      <dialog ref={dialogRef} className="custom-dialog">
        <Box textAlign="center" py={10} px={6}>
          <CheckCircleIcon boxSize={'50px'} color={'green.500'} />
          <Heading as="h2" size="xl" mt={6} mb={2}>
            Signup Successfull
          </Heading>
          <Text color={'gray.500'}>
            Please go to your email and click on the verification link sent
          </Text>
          <Link href='/' color={'blue.400'}>Click here to go to the login page</Link>
        </Box>
      </dialog>
    </Flex>
    
  )
}
