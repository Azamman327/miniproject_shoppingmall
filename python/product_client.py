from __future__ import print_function

import grpc
import product_pb2
import product_pb2_grpc

def login(userInfo):
    with grpc.insecure_channel('localhost:50052') as channel:
        stub = product_pb2_grpc.ProductInfoStub(channel)
        response = stub.login(product_pb2.UserInfo(
            userId=userInfo['userId'],
            password=userInfo['password']
        ))
        return response.userId

def signUp(userInfo):
    with grpc.insecure_channel('localhost:50052') as channel:
        stub = product_pb2_grpc.ProductInfoStub(channel)
        response = stub.signUp(product_pb2.UserInfo(
            userId=userInfo['userId'],
            password=userInfo['password']
        ))
        return response.userId

def getProductList():
    with grpc.insecure_channel('localhost:50052') as channel:
        stub = product_pb2_grpc.ProductInfoStub(channel)
        response = stub.getProductList(product_pb2.Empty(empty=''))
        return response.productList

def getCategory():
    with grpc.insecure_channel('localhost:50052') as channel:
        stub = product_pb2_grpc.ProductInfoStub(channel)
        response = stub.getCategory(product_pb2.Empty(empty=''))
        return response.categoryList

def createProduct(product):
    with grpc.insecure_channel('localhost:50052') as channel:
        stub = product_pb2_grpc.ProductInfoStub(channel)
        response = stub.createProduct(product_pb2.Product(
            productId = 0,
            categoryId=product['categoryId'],
            productName=product['name'],
            description=product['description'],
            price=product['price'],
            count=product['count']
        ))
        return response

def findByCategory(input):
    with grpc.insecure_channel('localhost:50052') as channel:
        stub = product_pb2_grpc.ProductInfoStub(channel)
        response = stub.findByCategory(product_pb2.CategoryId(value=input))
        return response.productList
    
def buyProduct(input):
    with grpc.insecure_channel('localhost:50052') as channel:
        stub = product_pb2_grpc.ProductInfoStub(channel)
        response = stub.findByName(product_pb2.ProductName(productName=input['productName']))
        print(response.productName)
        print(input['count'])
        print(input['userId'])

        stub.buyProduct(product_pb2.Buy(
            product=response, 
            count=input['count'], 
            userId=input['userId']
        ))
        return response

def findByName(input):
    with grpc.insecure_channel('localhost:50052') as channel:
        stub = product_pb2_grpc.ProductInfoStub(channel)
        response = stub.findByName(product_pb2.ProductName(productName=input))
        return response

def getPurchase(input):
    with grpc.insecure_channel('localhost:50052') as channel:
        stub = product_pb2_grpc.ProductInfoStub(channel)
        response = stub.getPurchase(product_pb2.UserId(userId=input))
        return response.purchaseList