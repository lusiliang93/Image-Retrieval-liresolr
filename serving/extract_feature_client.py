# coding=utf-8
"""Tensorflow Serving Client used for serving compare request.
"""
import sys
import time
import os

import argparse
from PIL import Image
import numpy as np
import tensorflow as tf
from grpc.beta import implementations
from tensorflow_serving.apis import predict_pb2
from tensorflow_serving.apis import prediction_service_pb2

def load_img(path, grayscale=False, target_size=None, interpolation='nearest'):
  if pil_image is None:
    raise ImportError('Could not import PIL.Image. '
                      'The use of `array_to_img` requires PIL.')
  img = pil_image.open(path)
  if img.mode != 'RGB':
      img = img.convert('RGB')
  if target_size is not None:
    width_height_tuple = (target_size[1], target_size[0])
    resample = pil_image.NEAREST
    img = img.resize(width_height_tuple, resample)
  return img

def img_to_array(img, data_format='channels_last'):
  x = np.asarray(img, dtype=np.float32)
  return x

def _preprocess_numpy_input(x,data_format,mode):
    x = x[::-1, ...]
    mean = [103.939, 116.779, 123.68]
    std = None
    # Zero-center by mean pixel
    x[0, :, :] -= mean[0]
    x[1, :, :] -= mean[1]
    x[2, :, :] -= mean[2]
    return x

def preprocess_input(x, data_format=None, mode='caffe'):
    data_format == 'channels_first'
    if isinstance(x, np.ndarray):
        return _preprocess_numpy_input(x, data_format=data_format, mode=mode)
    else:
        return _preprocess_symbolic_input(x, data_format=data_format, mode=mode)

def main(args):
    server = args.server
    host, port = server.split(':')
    channel = implementations.insecure_channel(host, int(port))
    stub = prediction_service_pb2.beta_create_PredictionService_stub(channel)
    img = args.image
    model_name = args.model_name
    signature_name = args.signature_name

    start = time.time()

    result = predict(img, stub, model_name, signature_name)
    end = time.time()

    print(result)
    print("Feature extraction time: ", str(end - start))


def predict(image_path, stub, model_name, signature_name):
    request = predict_pb2.PredictRequest()
    request.model_spec.name = model_name
    request.model_spec.signature_name = signature_name

    img = load_img(image_path, target_size=(224, 224))
    x = img_to_array(img)
    x = np.expand_dims(x, axis=0)
    x = preprocess_input(x)

    request.inputs['image'].CopyFrom(tf.contrib.util.make_tensor_proto(x, shape=x.shape))
    result = stub.Predict(request, 120.0)  # 5.0 seconds

    return result


def parse_arguments(argv):
    parser = argparse.ArgumentParser()

    parser.add_argument('server', type=str, help='host:port', default='172.31.131.16:9000')
    parser.add_argument('image', type=str, help='Image path to extract feature', default='172.31.131.16:9000')
    parser.add_argument('model_name', type=str, help='TF Serving model name', default='retrival')
    parser.add_argument('signature_name', type=str, help='Signature used to get result', default='prediction')


if __name__ == '__main__':
    main(parse_arguments(sys.argv[1:]))
