"""Convert keras model to tf serving servable model
"""
import os
import shutil

import tensorflow as tf
from keras import backend as K
from keras.applications.vgg16 import VGG16
from tensorflow.python.saved_model import builder as saved_model_builder
from tensorflow.python.saved_model import tag_constants

saved_model_dir = os.path.join('saved_model', str(1))
if os.path.exists(saved_model_dir):
    shutil.rmtree(saved_model_dir)

gpu_options = tf.GPUOptions(per_process_gpu_memory_fraction=0.8, allow_growth=False)
sess = tf.Session(config=tf.ConfigProto(gpu_options=gpu_options))
K.set_session(sess)

K._LEARNING_PHASE = tf.constant(0)
K.set_learning_phase(0)

VGG = VGG16(weights='imagenet')

prediction_signature = tf.saved_model.signature_def_utils.predict_signature_def(
    {"image": VGG.input}, {"prediction": VGG.output})

builder = saved_model_builder.SavedModelBuilder(saved_model_dir)
legacy_init_op = tf.group(tf.tables_initializer(), name='legacy_init_op')

# Initialize global variables and the model
init_op = tf.group(tf.global_variables_initializer(), tf.local_variables_initializer())
sess.run(init_op)

# Add the meta_graph and the variables to the builder
builder.add_meta_graph_and_variables(
    sess, [tag_constants.SERVING],
    signature_def_map={
        'prediction': prediction_signature,
    },
    legacy_init_op=legacy_init_op)
builder.save()

print('Successfully exported model to ', saved_model_dir)
