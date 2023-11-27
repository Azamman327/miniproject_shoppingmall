from concurrent import futures
import logging

import grpc
import product_pb2
import product_pb2_grpc

import pymysql

from datetime import datetime

class ProductInfoServicer(product_pb2_grpc.ProductInfoServicer):

    def login(self, request, context):
        conn = pymysql.connect(host='localhost', user='root', password='root', db='shop_db', charset='utf8')
        cur = conn.cursor()
        sql = "select password from User where userId = %s"
        val = request.userId
        cur.execute(sql, val)
        rslt = cur.fetchone()
        if rslt[0] == request.password:  
            print(datetime.now(), ': 유저', request.userId, '가 로그인')
            return product_pb2.UserId(userId=request.userId)
        else:
            print(datetime.now(), ': 로그인 실패')
            return product_pb2.UserId(userId='')
    
    def logout(self, request, context):
        return

    def signUp(self, request, context):
        conn = pymysql.connect(host='localhost', user='root', password='root', db='shop_db', charset='utf8')
        cur1 = conn.cursor()
        sql1 = 'select COUNT(*) from User where userId = %s'
        val1 = request.userId
        cur1.execute(sql1, val1)
        rslt = cur1.fetchone()

        print(rslt[0])
        if rslt[0] == 0:
            cur2 = conn.cursor()
            sql2 = 'insert into User values(%s, %s)'
            val2 = (request.userId, request.password)
            cur2.execute(sql2, val2)
            conn.commit()
            print(datetime.now(), ': 유저', request.userId, '가 회원가입')
            return product_pb2.UserId(userId=request.userId)
        else:
            return product_pb2.UserId(userId='')

    def getProductList(self, request, context):
        conn = pymysql.connect(host='localhost', user='root', password='root', db='shop_db', charset='utf8')
        cur = conn.cursor()
        sql = "select * from Product"
        cur.execute(sql)
        rslt = cur.fetchall()
        print(datetime.now(), ': 상품 리스트 조회')

        getList = []
        for data in rslt:
            getList.append(
                product_pb2.Product(
                    productId=data[0],
                    categoryId=data[1],
                    productName=data[2],
                    description=data[3],
                    price=data[4],
                    count=data[5]
                )
            )
        
        return product_pb2.ProductList(productList=getList)

    def getCategory(self, request, context):
        conn = pymysql.connect(host='localhost', user='root', password='root', db='shop_db', charset='utf8')
        cur = conn.cursor()
        sql = "select * from Category"
        cur.execute(sql)
        rslt = cur.fetchall()
        print(datetime.now(), ': 카테고리 리스트 조회')

        getList = []
        for data in rslt:
            getList.append(
                product_pb2.Category(
                    categoryId=data[0],
                    name=data[1]
                )
            )

        return product_pb2.CategoryList(categoryList=getList)

    def createProduct(self, request, context):
        conn = pymysql.connect(host='localhost', user='root', password='root', db='shop_db', charset='utf8')
        cur = conn.cursor()
        sql = "insert into PRODUCT values(NULL, %s, %s, %s, %s, %s)"
        vals = (
            request.categoryId,
            request.productName, 
            request.description, 
            request.price, 
            request.count
        )
        cur.execute(sql, vals)
        conn.commit()
        print(datetime.now(), ':', '새로운 상품', request.productName, '을', request.categoryId, '번 카테고리에', request.count, '개 등록' )
        return product_pb2.Empty(empty='')
    
    def findByCategory(self, request, context):
        conn = pymysql.connect(host='localhost', user='root', password='root', db='shop_db', charset='utf8')
        cur = conn.cursor()
        sql = "select * from Product where categoryId = %s"
        vals = request.value
        cur.execute(sql, vals)
        rslt = cur.fetchall()
        print(datetime.now(), ':', request.value, '에 속한 상품 리스트를 조회함')

        getList = []
        for data in rslt:
            getList.append(
                product_pb2.Product(
                    productId=data[0],
                    categoryId=data[1],
                    productName=data[2],
                    description=data[3],
                    price=data[4],
                    count=data[5]
                )
            )

        return product_pb2.ProductList(productList=getList)
    
    def buyProduct(self, request, context):
        conn = pymysql.connect(host='localhost', user='root', password='root', db='shop_db', charset='utf8')
        cur1 = conn.cursor()
        sql1 = "update PRODUCT SET count = count - %s where productId = %s"
        vals1 = (request.count, request.product.productId)
        cur1.execute(sql1, vals1)
        conn.commit()

        totalPrice = request.count * request.product.price

        conn = pymysql.connect(host='localhost', user='root', password='root', db='shop_db', charset='utf8')
        cur2 = conn.cursor()
        sql2 = "insert into Purchase values(NULL, %s, %s, %s, %s, %s)"
        vals2 = (
            request.userId, 
            request.product.productId, 
            request.product.productName, 
            request.count, 
            totalPrice
        )
        cur2.execute(sql2, vals2)
        conn.commit()

        print(datetime.now(), ':', request.userId, '가 productId(', request.product.productId, ')상품을', request.count, '개 구매')
        return product_pb2.Empty(empty='')
    
    def findByName(self, request, context):
        conn = pymysql.connect(host='localhost', user='root', password='root', db='shop_db', charset='utf8')
        cur = conn.cursor()
        sql = "select * from Product where productName = %s"
        vals = request.productName
        cur.execute(sql, vals)
        rslt = cur.fetchone()
        print(datetime.now(), ':', request.productName, '키워드로 상품 검색')

        return product_pb2.Product(
            productId=rslt[0],
            categoryId=rslt[1],
            productName=rslt[2],
            description=rslt[3],
            price=rslt[4],
            count=rslt[5]
        )
    
    def getPurchase(self, request, context):
        conn = pymysql.connect(host='localhost', user='root', password='root', db='shop_db', charset='utf8')
        cur = conn.cursor()
        sql = "select * from Purchase where userId = %s"
        vals = request.userId
        cur.execute(sql, vals)
        rslt = cur.fetchall()
        print(datetime.now(), ':', request.userId, '의 구매기록 조회')

        getList = []
        for data in rslt:
            getList.append(
                product_pb2.Purchase(
                    purchaseId=data[0],
                    userId=data[1],
                    productId=data[2],
                    productName=data[3],
                    count=data[4],
                    totalPrice=data[5]
                )
            )        

        return product_pb2.PurchaseList(purchaseList=getList)

def serve():
    port = '50052'
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    product_pb2_grpc.add_ProductInfoServicer_to_server(ProductInfoServicer(), server)
    server.add_insecure_port('[::]:' + port)
    server.start()
    print("Server started, listening on " + port)
    server.wait_for_termination()


if __name__ == '__main__':
    logging.basicConfig()
    serve()
