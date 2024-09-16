import { createStandaloneToast } from '@chakra-ui/react'

const { toast } = createStandaloneToast()

const Notification = (title,description,status)=>{
    toast({
        title,
        description,
        status,
        isClosable: true,
        duration: 4000
      })
}

export const successNotification = (title,description)=>{
    Notification(
        title,
        description,
        "success"
    )
}

export const errorNotification = (title,description)=>{
    Notification(
        title,
        description,
        "error"
    )
}