import numpy as np
import tensorflow as tf
import keras

from keras import backend as K
from keras.layers.core import Flatten, Dense, Dropout, Lambda
from keras.models import Model
from keras.preprocessing import image
from keras.optimizers import SGD, RMSprop, Adam, Nadam

import os
from os import listdir
from os.path import join

gpu_options = tf.GPUOptions(per_process_gpu_memory_fraction=0.8, allow_growth=False)
sess = tf.Session(config=tf.ConfigProto(gpu_options=gpu_options))

# Missing this was the source of one of the most challenging an insidious bugs that I've ever encountered.  
# Without explicitly linking the session the weights for the dense layer added below don't get loaded
# and so the model returns random results which vary with each model you upload because of random seeds.
K.set_session(sess)

# Use this only for export of the model.  
# This must come before the instantiation of ResNet50
K._LEARNING_PHASE = tf.constant(0)
K.set_learning_phase(0)

'''
# Transfer learning from ResNet50
Resnet50model = ResNet50(include_top=True, weights='imagenet', input_tensor=None, input_shape=None)

# Pop off the last layer
Resnet50model.layers.pop()
Resnet50model.layers[-1].outbound_nodes = []
Resnet50model.outputs = [Resnet50model.layers[-1].output]

# Add a layer with the correct number of dimensions
x = Resnet50model.outputs
preds = Dense(num_outputs, activation='softmax',name='prediction')(x)
Resnet50model = Model(Resnet50model.input, preds)

# Training Not included; We're going to load pretrained weights
Resnet50model.load_weights('weights.hdf5')
'''
ResNet50model = VGG16(weights='imagenet')

# Import the libraries needed for saving models
# Note that in some other tutorials these are framed as coming from tensorflow_serving_api which is no longer correct
from tensorflow.python.saved_model import builder as saved_model_builder
from tensorflow.python.saved_model import tag_constants, signature_constants, signature_def_utils_impl

# I want the full prediction tensor out, not classification. This format: {"image": Resnet50model.input} took me a while to track down
prediction_signature = tf.saved_model.signature_def_utils.predict_signature_def({"image": Resnet50model.input}, {"prediction":Resnet50model.output})

# export_path is a directory in which the model will be created
export_path = os.path.join('serving', str(1))
if os.path.exists(export_path):
    shutil.rmtree(export_path)
    os.mkdir(export_path)

builder = saved_model_builder.SavedModelBuilder(export_path)
legacy_init_op = tf.group(tf.tables_initializer(), name='legacy_init_op')

# Initialize global variables and the model
init_op = tf.group(tf.global_variables_initializer(), tf.local_variables_initializer())
sess.run(init_op)

# Add the meta_graph and the variables to the builder
builder.add_meta_graph_and_variables(
      sess, [tag_constants.SERVING],
      signature_def_map={
           signature_constants.DEFAULT_SERVING_SIGNATURE_DEF_KEY:
               prediction_signature,
      },
      legacy_init_op=legacy_init_op)
# save the graph      
builder.save()      