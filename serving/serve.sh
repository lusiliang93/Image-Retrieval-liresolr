#!/usr/bin/env bash
tensorflow_model_server \
--port=9000 \
--model_name=retrival \
--model_base_path=/home/liuhy/Data/cv-projects/ImageRetrival/serving/saved_model \
